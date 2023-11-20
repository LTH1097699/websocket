package io.lth.websocket.controller;

import io.lth.websocket.domain.dto.ChatMessageResponse;
import io.lth.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WSChatController {

    private final SimpMessagingTemplate websocket;

    private final WebSocketSessionManager webSocketSessionManager;

    @MessageMapping("/send")
    @SendTo("/topic/message")
    public ChatMessageResponse send(@Payload ChatMessageResponse request) {
        return request;
    }

    @MessageMapping("/chat")
    public ChatMessageResponse chat(@Payload ChatMessageResponse request) {
//        websocket.convertAndSendToUser();
//        websocket.convertAndSend();
        return request;
    }
}
