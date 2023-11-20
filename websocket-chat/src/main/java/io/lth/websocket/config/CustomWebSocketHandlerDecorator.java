package io.lth.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.Optional;

@Slf4j
public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    private final String lineBreak;

    public CustomWebSocketHandlerDecorator(WebSocketHandler webSocketHandler) {
        super(webSocketHandler);
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            lineBreak = "\r\n";
        } else {
            lineBreak = "\n";
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, updateBodyIfNeeded(message));
    }

    /**
     * Updates the content of the specified message. The message is updated only if it is
     * a {@link TextMessage text message} and if does not contain the <tt>null</tt> character at the end. If
     * carriage returns are missing (when the command does not need a body) there are also added.
     */
    private WebSocketMessage<?> updateBodyIfNeeded(WebSocketMessage<?> message) {
        if (!(message instanceof TextMessage castMessage) || castMessage.getPayload().endsWith("\u0000")) {
            return message;
        }

        String payload = ((TextMessage) message).getPayload();

        final Optional<StompCommand> stompCommand = getStompCommand(payload);

        if (stompCommand.isEmpty()) {
            return message;
        }

        if (!stompCommand.get().isBodyAllowed() && !payload.endsWith(lineBreak.concat(lineBreak))) {
            if (payload.endsWith(lineBreak)) {
                payload += lineBreak;
            } else {
                payload += lineBreak.concat(lineBreak);
            }
        }

        payload += "\u0000";

        return new TextMessage(payload);
    }

    private Optional<StompCommand> getStompCommand(String payload) {
        final int firstCarriageReturn = payload.indexOf(lineBreak);

        if (firstCarriageReturn < 0) {
            return Optional.empty();
        }

        try {
            return Optional.of(StompCommand.valueOf(payload.substring(0, firstCarriageReturn)));
        } catch (IllegalArgumentException e) {
            log.trace("Error while parsing STOMP command.", e);
            return Optional.empty();
        }
    }
}
