package kr.pianobear.application.controller;

import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Notification;
import kr.pianobear.application.service.NotificationService;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private final List<SseEmitter> emitters = new ArrayList<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

    @GetMapping("/")
    public List<Notification> getNotifications() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Member receiver = new Member(currentUserId);
        return notificationService.getNotifications(receiver);
    }

    @GetMapping("/count")
    public long getNotificationCount() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Member receiver = new Member(currentUserId);
        return notificationService.getNotificationCount(receiver);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    @DeleteMapping("/clear")
    public void clearNotifications() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Member receiver = new Member(currentUserId);
        notificationService.clearNotifications(receiver);
    }

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
