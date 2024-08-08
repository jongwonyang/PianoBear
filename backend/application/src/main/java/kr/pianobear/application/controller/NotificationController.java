package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Notification;
import kr.pianobear.application.service.NotificationService;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification", description = "Notification management API")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private final List<SseEmitter> emitters = new ArrayList<>();

    @Value("${jwt.access-expiration-time}")
    private int connectionTimeOut;

    @Operation(summary = "알림 받는 설정", description = "suscribe를 통해 알림을 받을 수 있게 된다")
    @GetMapping(value="/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE )
    public SseEmitter subscribe() throws Exception {
        SseEmitter emitter = new SseEmitter(connectionTimeOut*1000L);
        emitters.add(emitter);

        try {
                emitter.send(SseEmitter.event()
                        .name("connect")
                        .data("connected!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));



        return emitter;
    }

    @Operation(summary = "알림 불러오기", description = "받는 사용자의 알림 목록을 불러온다")
    @GetMapping("/")
    public List<Notification> getNotifications() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Member receiver = new Member(currentUserId);
        return notificationService.getNotifications(receiver);
    }

    @Operation(summary = "알림 개수 불러오기", description = "보유 알림 개수를 불러옴으로서 개수 저장가능")
    @GetMapping("/count")
    public long getNotificationCount() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Member receiver = new Member(currentUserId);
        return notificationService.getNotificationCount(receiver);
    }

    @Operation(summary = "특정 알림 삭제", description = "거절함으로서 알림 삭제 가능")
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    @Operation(summary = "전체 알림 삭제", description = "전체 알림 삭제가 가능함")
    @DeleteMapping("/clear")
    public void clearNotifications() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Member receiver = new Member(currentUserId);
        notificationService.clearNotifications(receiver);
    }

    @Operation(summary = "알림을 보낸다", description = "service에서 사용")
    public void sendNotificationToClients(String message) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
