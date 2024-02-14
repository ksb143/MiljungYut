package com.ssafy.hungry.domain.miniGame.controller;

import com.ssafy.hungry.domain.game.dto.GameChatDto;
import com.ssafy.hungry.domain.miniGame.dto.MiniGameDto;
import com.ssafy.hungry.global.dto.StompDataDto;
import com.ssafy.hungry.global.service.RedisSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StompMiniGameController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(value="/pick/{roomCode}/mini-game-start")
    public void startMiniGame(@DestinationVariable String roomCode, MiniGameDto miniGameDto){
       miniGameDto.setActionCategory(7);
       simpMessagingTemplate.convertAndSend("/sub/game/" + roomCode, miniGameDto);
    }

    @MessageMapping(value="/pick/{roomCode}/mini-game-finish")
    public void finishMiniGame(@DestinationVariable String roomCode, MiniGameDto miniGameDto){
        miniGameDto.setActionCategory(8);
        simpMessagingTemplate.convertAndSend("/sub/game/" + roomCode, miniGameDto);
    }


}