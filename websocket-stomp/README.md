# WebSocket STOMP

_-_ STOMP is only a formatter for payload in WebSocket, design for working with MON (Messsage Oriented MiddleWare) patten.\
_-_ STOMP use WebSocket like transport to send message.\
_-_ Because STOMP is just a formatter, so it does not take care about Websocket handshake (`after create WebSocket handshake, we send CONNECT command to create STOMP connection`)

#### - Dependencies:
1. "org.springframework.boot:spring-boot-starter-web:2.5.6"
2. "org.springframework.boot:spring-boot-starter-websocket:2.5.6"
---

#### - Demo

**Test Postman**\
_#_ CONNECT
```
>>
CONNECT
accept-version:1.2
host:localhost
content-length:0

<<
CONNECTED
version:1.2
heart-beat:0,0

  (this is null)
```
_#_ SUBSCRIBE
```
>>
SUBSCRIBE
destination:/topic/message
id:sub-1
ack:client
content-length:0

<<
```
_#_ SEND
```
>>
SEND
destination:/app/send

{"text":"text"}

<<
MESSAGE
destination:/topic/message
content-type:application/json
subscription:sub-0
message-id:54ab20fe-ae1e-369a-368c-3f7b48a3f16a-3
content-length:15

{"text":"5678"}
```
---
#### - Explaining
**Config WebSocket for STOMP message**

_-_ Implement `WebSocketMessageBrokerConfigurer` interface
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
```
> This interface provide method to create endpoint for connect WebSocket, message broker, app destination.

```java
@Override
public void configureMessageBroker(MessageBrokerRegistry registry)
    registry.enableSimpleBroker("/topic"); // is in-memory broker
    registry.setApplicationDestinationPrefixes("/app");

@Override
public void registerStompEndpoints(StompEndpointRegistry registry)
    registry.addEndpoint("/endpoint");
```
_-_ `/endpoint` path to connect to WebSocket\
_-_ `/topic` channel for client subscribe (it can be multiple like `/topic, /topic2`)
1. Using with @MessageMapping\
    `@MessageMapping(/channel1)` >> destination `/topic/channel1`

**Problem**\
_-_ When I send with postman, I can not pass a null octet `(I have tried with ^@, \u0000)`.\
_- Error_ `org.springframework.messaging.simp.stomp.StompConversionException: Frame must be terminated with a null octet`.\
_-_ When I test with CLI like cURL, websocat, I can pass a null octet `(In linux we press Ctrl + Shift + 2 to add null octet, and it print ^@ that diff with when I type ^@ :)), don't know why)` but I can not pass a breakline `(I'm not sure but have tried with \n)`.\
_- Error_ `org.springframework.messaging.simp.stomp.StompConversionException: Illegal escape sequence`.\
_-_ After struggling with this, because information I can find in Internet that is they use with javascript library and I do not want to use that. Fortunately, at the evening, around at 9 P.M, I still turn around on Internet with keyword `test stomp in command line`, finally I found a solution (hate me that is simple) [*# source*](https://stackoverflow.com/questions/47373402/spring-stomp-incomplete-frame), we must create a Handler Decorator to add this s**t `null octet` when it not contains in payload (So why `\u0000` place in PostMan not working, but in code, hope me can find out).

_-_ Method can not get index of char `\n` when send by curl in linux `(str.indexOf("\n") || str.indexOf('\n'))`

**Config WebSocket Handler**\
_-_ Implement `WebSocketHandlerDecorator` interface
```java
public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {
```
_-_ And add it to config
```java
@Override
public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    registry.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
        @Override
        public WebSocketHandler decorate(WebSocketHandler handler) {
            return new CustomWebSocketHandlerDecorator(handler);
        }
    }); }
```

_# Preferences_
- [*# STOMP Protocol Specification, Version 1.2*](https://stomp.github.io/stomp-specification-1.2.html#SUBSCRIBE)
- [*# spring boot websocket client*](https://github.com/nielsutrecht/spring-boot-websocket-client)

