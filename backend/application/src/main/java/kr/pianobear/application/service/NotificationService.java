package kr.pianobear.application.service;

import kr.pianobear.application.dto.NotificationDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Notification;
import kr.pianobear.application.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

//
private final List<SseEmitter> emitters = new ArrayList<>();

    public void createNotification(Member receiver, String type, String content) {
        Notification notification = new Notification(receiver, type, content);
        notificationRepository.save(notification);
//        sendNotificationToClient(receiver.getId(), NotificationDTO.fromNotification(notification));
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
//        emitters.put(receiver.getId(), emitter);
        emitters.add(emitter);

        emitter.onCompletion(() -> {
            emitters.remove(receiver.getId());
            System.out.println("Emitter completed for user: " + receiver.getId());
        });

        emitter.onTimeout(() -> {
            emitters.remove(receiver.getId());
            System.out.println("Emitter timed out for user: " + receiver.getId());
        });

        emitter.onError((e) -> {
            System.err.println("SSE connection error: " + e.getMessage());
            emitters.remove(receiver.getId());
        });

        // 클라이언트가 연결될 때 밀린 알림 전송
        List<NotificationDTO> missedNotifications = getNotifications(receiver);
        for (NotificationDTO notification : missedNotifications) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                System.err.println("Failed to send missed notification: " + e.getMessage());
                emitters.remove(receiver.getId());
                break;
            }
        }

        return emitter;
    }

    @Async
    public void sendNotificationToClients(NotificationDTO notification) {
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
//    public void sendNotificationToClient(String userId, NotificationDTO notification) {
////        SseEmitter emitter = emitters.get(userId);
//        List<SseEmitter> deadEmitters = new ArrayList<>();
//        if (emitter != null) {
//            try {
//                emitter.send(SseEmitter.event().name("notification").data(notification));
//                System.out.println("Notification sent to user: " + userId);
//            } catch (IOException e) {
//                emitters.remove(userId);
//                System.err.println("Failed to send notification to user: " + userId + ". Connection might be closed.");
//            }
//        } else {
//            System.out.println("No active connection found for user: " + userId);
//        }
//    }


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
