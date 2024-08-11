package kr.pianobear.application.repository;

import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiver(Member receiver);
    long countByReceiver(Member receiver);
}
