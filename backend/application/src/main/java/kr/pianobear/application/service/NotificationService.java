package kr.pianobear.application.service;

import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Notification;
import kr.pianobear.application.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(Member receiver, String type, String content) {
        Notification notification = new Notification(receiver, type, content);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(Member receiver) {
        return notificationRepository.findByReceiverAndRead(receiver, false);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void clearNotifications(Member receiver) {
        List<Notification> notifications = notificationRepository.findByReceiverAndRead(receiver, false);
        notificationRepository.deleteAll(notifications);
    }
}
