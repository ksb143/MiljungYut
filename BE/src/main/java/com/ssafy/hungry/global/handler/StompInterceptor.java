package com.ssafy.hungry.global.handler;

import com.ssafy.hungry.global.entity.SessionEntity;
import com.ssafy.hungry.global.entity.StompPrincipal;
import com.ssafy.hungry.global.repository.SessionRepository;
import com.ssafy.hungry.global.util.JWTUtil;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StompInterceptor implements ChannelInterceptor {
    private final JWTUtil jwtUtil;
    private final SessionRepository sessionRepository;

    public StompInterceptor(JWTUtil jwtUtil, SessionRepository sessionRepository){
        this.jwtUtil = jwtUtil;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //구독할 때 토큰 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 헤더 토큰 얻기
            String authorizationHeader = String.valueOf(accessor.getNativeHeader("Authorization"));

            System.out.println(accessor.getUser().getName());


            if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
                System.out.println("소켓 통신 토큰 없음");
                throw new MessageDeliveryException("메세지 예외");
            }

            String token = authorizationHeader.split(" ")[1];
            // 토큰 인증

            try{
                if(jwtUtil.validateToken(token)){
                    System.out.println("소켓통신에서 토큰 검증");
                    String email = jwtUtil.getUserId(token);
                    StompPrincipal user = new StompPrincipal(accessor.getUser().getName());
                    sessionRepository.save(new SessionEntity(email, user));
                }
            }catch (MessageDeliveryException e){
                throw new MessageDeliveryException("메세지 에러");
            }catch (MalformedJwtException e){
                throw new MessageDeliveryException("예외3");
            }

        }
        return message;
    }
}

