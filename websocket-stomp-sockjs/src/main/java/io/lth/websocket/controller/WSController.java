package io.lth.websocket.controller;

import io.lth.websocket.domain.dto.ChatMessageResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WSController {

    @GetMapping("/sockjs-broadcast") // Todo: test for working properly
    public String getWebSocketWithSockJsBroadcast() {
        return "sockjs-test.html";
    }

    @MessageMapping("/send")
    @SendTo("/topic/message")
    public ChatMessageResponse send(@Payload ChatMessageResponse request) {
        return request;
    }
}
