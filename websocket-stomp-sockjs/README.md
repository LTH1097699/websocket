# WebSocket STOMP With SockJS

#### - Demo

Run test class

Reference:\
[*# doc sockjs-protocol*](https://sockjs.github.io/sockjs-protocol/sockjs-protocol-0.3.3.html)

`Remove Session when compare with disconnect deplay`
```java
public class TransportHandlingSockJsService extends AbstractSockJsService implements SockJsServiceConfig, Lifecycle {
    private void scheduleSessionTask() ...
        if (session.getTimeSinceLastActive() > getDisconnectDelay()) 
            this.sessions.remove(session.getId());
        ...
```

> curl -v --no-buffer --location 'http://localhost:8080/endpoint/122/1/websocket'  --header 'Upgrade: websocket' --header 'Connection: keep-alive, Upgrade' --header 'Sec-WebSocket-Version: 13' --header 'Sec-WebSocket-Key: ATAxkP9wOtJoYA64cuQX+Q==' --header 'Sec-WebSocket-Extensions: permessage-deflate' --header 'host: localhost:8080' --header 'Origin: http://localhost:8080' --output 123

**Configuration**\
@EnableWebSocketMessageBroker

`FrameworkServlet` -> processRequest() -> `DispatcherServlet[implement]`doServlet() ->  doDispatch { `HandlerAdapter` -> handle()} ->  `HandlerAdapter` -> handle(){ `Object SockJsHttpRequestHandler` -> handleRequest()} -> `SockJsHttpRequestHandler` -> handleRequest(){ `Inject AbstractSockJsService` -> handleRequest()}

//To do
continue step server handle request\
do step client send request

sockjs send /info to get state of websocket

if state websocket is false -> do xhr streaming protocol

xhr stream send /server_id/session_id/xhr_streaming (server_id[000-999], session(random and unique), transport(xhr_streaming)) with header host:localhost:8080, heart-beat:0,0 -> receive response status 204(success) -> server send heart-beat to client (do not know how to receive from curl, using java client or sockjs[library javascript in font-end] is success, can reference to write to file [# 1](https://stackoverflow.com/questions/39321958/retrieve-post-response-data-with-curl))

next xhr stream send /server_id/session_id/xhr_send (header same), body[raw/json] list<String> (["CONNECT\nheart-beat:0,0\naccept-version:1.1,1.2\n\n\u0000"]) -> sever response status 204

next use xhr_send sent body ["SUBSCRIBE\nid:sub-0\ndestination:/topic/message\n\n\u0000"] and ["SEND\ndestination:/topic\ncontent-length:12\n\nMESSAGE TEST\u0000"] 

explain test integrate\
learn more about DefaultStompFrameHandler