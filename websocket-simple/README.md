# WebSocket Simple

_-_ This is simple broadcast, when one session send message, this message will send to all session in WebSocketSession list

#### - Dependencies:
1. "org.springframework.boot:spring-boot-starter-web:2.5.6"
2. "org.springframework.boot:spring-boot-starter-websocket:2.5.6"
---

#### - Demo

**Test connection with cUrl**
```
curl -v --include --header "Connection: Upgrade" --header "Upgrade: websocket" --header "Sec-WebSocket-Key: qwerty" --header "Sec-WebSocket-Version: 13" http://localhost:8080/ws
```

**Test Postman**
- create multiple connections and send message

---
#### - Explaining
**Create Handler for socket**\
_-_ Create a customized Handler by extend abstract `AbstractWebSocketHandler` class, or subclass like `TextWebSocketHandler` for process text message only, `BinaryWebSocketHandler` for process binary message only.
```java
public class WebSocketHandler extends AbstractWebSocketHandler {
```

_-_ When have request upgrade, connection will keep in session list by override method of `AbstractWebSocketHandler`
```java
List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

@Override
void afterConnectionEstablished(WebSocketSession session)
        sessions.add(session);

@Override
void afterConnectionClosed(WebSocketSession session, CloseStatus status)
        sessions.remove(session);

@Override
void handleTextMessage(WebSocketSession session, TextMessage message)
        sessions.forEach(sessionS -> {
            sessionS.sendMessage(message);
        });
```
**Config WebSocket**

_-_ Implement `WebSocketConfigurer` interface
```java
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
```
> This interface provide method to registering handler, if registry do not have handler it will ignore request from client

_-_ Add handler and path
```java
registry.addHandler(new WebSocketHandler(), "/web-socket");
```
> After registering handler, Whenever have request to this path `/web-socket`, register will catch and let `the registered handler` work with it

use-case [1](https://stackoverflow.com/questions/49378759/path-parameters-in-websocketconfigurer-addhandler-in-spring)