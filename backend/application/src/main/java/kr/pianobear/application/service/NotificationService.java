package kr.pianobear.application.service;

import kr.pianobear.application.dto.NotificationDTO;
import kr.pianobear.application.model.FriendRequest;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Notification;
import kr.pianobear.application.repository.FriendRequestRepository;
import kr.pianobear.application.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

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
        // 1. 알림을 먼저 조회
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);

        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();

            // 2. 연결된 FriendRequest가 있는지 확인하고 삭제
            FriendRequest friendRequest = notification.getFriendRequest();
            if (friendRequest != null) {
                friendRequestRepository.delete(friendRequest);
            }

            // 3. 알림 삭제
            notificationRepository.deleteById(notificationId);
        }
    }

    // 전체 알림 삭제 메서드도 동일하게 수정할 수 있습니다
    public void clearNotifications(Member receiver) {
        List<Notification> notifications = notificationRepository.findByReceiver(receiver);
        for (Notification notification : notifications) {
            // 연결된 FriendRequest가 있는지 확인하고 삭제
            FriendRequest friendRequest = notification.getFriendRequest();
            if (friendRequest != null) {
                friendRequestRepository.delete(friendRequest);
            }
        }
        // 알림 삭제
        notificationRepository.deleteAll(notifications);
    }
}