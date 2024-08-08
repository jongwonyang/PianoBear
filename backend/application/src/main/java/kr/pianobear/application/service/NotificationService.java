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

    public List<Notification> getNotifications(Member receiver) {
        return notificationRepository.findByReceiver(receiver);
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
}
