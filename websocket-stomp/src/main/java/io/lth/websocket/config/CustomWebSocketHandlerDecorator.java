package io.lth.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.Optional;

/**
 * Extension of the {@link WebSocketHandlerDecorator websocket handler decorator} that allows to manually test the
 * STOMP protocol.
 *
 * @author Sebastien Gerard
 */
@Slf4j
public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    public CustomWebSocketHandlerDecorator(WebSocketHandler webSocketHandler) {
        super(webSocketHandler);
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
        if (!(message instanceof TextMessage) || ((TextMessage) message).getPayload().endsWith("\u0000")) {
            return message;
        }

        String payload = ((TextMessage) message).getPayload();

        final Optional<StompCommand> stompCommand = getStompCommand(payload);

        if (stompCommand.isEmpty()) {
            return message;
        }

        if (!stompCommand.get().isBodyAllowed() && !payload.endsWith("\n\n")) {
            if (payload.endsWith("\n")) {
                payload += "\n";
            } else {
                payload += "\n\n";
            }
        }

        payload += "\u0000";

        return new TextMessage(payload);
    }

    /**
     * Returns the {@link StompCommand STOMP command} associated to the specified payload.
     */
    private Optional<StompCommand> getStompCommand(String payload) {
        final int firstCarriageReturn = payload.indexOf('\n');

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
