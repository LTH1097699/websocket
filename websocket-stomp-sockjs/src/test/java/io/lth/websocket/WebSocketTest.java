package io.lth.websocket;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebSocketSTOMPSockJsApplication.class)
//@WebIntegrationTest is deprecated by SpringBootTest
public class WebSocketTest {

    static final String WEBSOCKET_URI = "ws://localhost:8080/endpoint";
    static final String WEBSOCKET_TOPIC = "/topic";

    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;
    WebSocketStompClient xhrStompClient;

    @Before
    public void setup() {
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        xhrStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new RestTemplateXhrTransport())));
    }

    @Test
    public void shouldReceiveAMessageFromTheServer() throws Exception {

        StompSession session = stompClient
                .connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {})
                .get(1, SECONDS);
        session.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler());

        String message = "MESSAGE TEST";
        session.send(WEBSOCKET_TOPIC, message.getBytes());

        Assert.assertEquals(message, blockingQueue.poll(1, SECONDS));
    }

    @Test
    public void shouldReceiveAMessageFromTheServerThroughXhrStream() throws Exception {

        StompSession session = xhrStompClient
                .connect(WEBSOCKET_URI, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {})
                .get(1, HOURS);
        session.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler());

        String message = "MESSAGE TEST";
        session.send(WEBSOCKET_TOPIC, message.getBytes());

        Assert.assertEquals(message, blockingQueue.poll(1, SECONDS));
    }

    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer(new String((byte[]) o));
        }
    }
}
