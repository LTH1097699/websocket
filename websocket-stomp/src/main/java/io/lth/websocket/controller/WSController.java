package io.lth.websocket.controller;

import io.lth.websocket.domain.dto.ChatMessageResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WSController {

    @MessageMapping("/send")
    @SendTo("/topic/message")
    public ChatMessageResponse send(@Payload ChatMessageResponse request) {
        return request;
    }
}
