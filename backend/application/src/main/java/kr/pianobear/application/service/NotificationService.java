package kr.pianobear.application.service;

import kr.pianobear.application.dto.NotificationDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Notification;
import kr.pianobear.application.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 알림 생성 및 전송
    public void createNotification(Member receiver, String type, String content) {
        Notification notification = new Notification(receiver, type, content);
        notificationRepository.save(notification);

        NotificationDTO notificationDTO = NotificationDTO.fromNotification(notification);
        sendNotificationToClient(receiver.getId(), notificationDTO);
    }

    // 특정 사용자에게 알림 전송
    public void sendNotificationToClient(String userId, NotificationDTO notification) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }

    // 알림 개수 업데이트 전송
    public void sendNotificationCountUpdate(String userId, long count) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notificationCount", count);
    }

    // 특정 사용자에 대한 알림 가져오기
    public List<NotificationDTO> getNotifications(Member receiver) {
        List<Notification> notifications = notificationRepository.findByReceiver(receiver);
        return notifications.stream()
                .map(NotificationDTO::fromNotification)
                .collect(Collectors.toList());
    }

    // 알림 개수 가져오기
    public long getNotificationCount(Member receiver) {
        return notificationRepository.countByReceiver(receiver);
    }

    // 알림 삭제
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // 모든 알림 삭제
    public void clearNotifications(Member receiver) {
        List<Notification> notifications = notificationRepository.findByReceiver(receiver);
        notificationRepository.deleteAll(notifications);
    }
}
