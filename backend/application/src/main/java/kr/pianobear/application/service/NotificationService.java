package kr.pianobear.application.service;

import kr.pianobear.application.dto.NotificationDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Notification;
import kr.pianobear.application.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    private final List<SseEmitter> emitters = new ArrayList<>();

    public void createNotification(Member receiver, String type, String content) {
        Notification notification = new Notification(receiver, type, content);
        notificationRepository.save(notification);
        sendNotificationToClients(NotificationDTO.fromNotification(notification));
    }

    public List<NotificationDTO> getNotifications(Member receiver) {
        List<Notification> notifications = notificationRepository.findByReceiver(receiver);
        return notifications.stream()
                .map(NotificationDTO::fromNotification)
                .collect(Collectors.toList());
    }

    public long getNotificationCount(Member receiver) {
        return notificationRepository.countByReceiver(receiver);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void clearNotifications(Member receiver) {
        List<Notification> notifications = notificationRepository.findByReceiver(receiver);
        notificationRepository.deleteAll(notifications);
    }

    public SseEmitter addEmitter(Member receiver, int connectionTimeOut) {
        SseEmitter emitter = new SseEmitter(connectionTimeOut * 1000L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> {
            System.err.println("SSE connection error: " + e.getMessage());
            emitters.remove(emitter);
        });

        // 클라이언트가 연결될 때 밀린 알림 전송
        List<NotificationDTO> missedNotifications = getNotifications(receiver);
        for (NotificationDTO notification : missedNotifications) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                // 전송 실패 시 해당 emitter 제거 및 로그 기록
                System.err.println("Failed to send missed notification: " + e.getMessage());
                emitters.remove(emitter);
                break;
            }
        }

        return emitter;
    }



    private void sendNotificationToClients(NotificationDTO notification) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }

    public void sendNotificationCountUpdate(long count) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("notificationCount").data(count));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
