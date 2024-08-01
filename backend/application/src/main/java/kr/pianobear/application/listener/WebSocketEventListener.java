package kr.pianobear.application.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
public class WebSocketEventListener {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public WebSocketEventListener(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        String destination = headerAccessor.getDestination();

        if (user != null && "/topic/online-status-tracker".equals(destination)) {
            String username = user.getName();
            redisTemplate.opsForValue().set("user:online:" + username, true);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println(event);
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        if (user != null) {
            String userId = user.getName();
            redisTemplate.delete("user:online:" + userId);
        }
    }
}
