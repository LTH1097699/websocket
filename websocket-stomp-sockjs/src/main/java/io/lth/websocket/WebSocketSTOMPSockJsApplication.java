package io.lth.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class WebSocketSTOMPSockJsApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSocketSTOMPSockJsApplication.class, args);
        try {
            System.out.println(InetAddress.getLocalHost().getHostName());
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
