package kr.pianobear.application.dto;

import kr.pianobear.application.model.Notification;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private String receiverId;
    private String type;
    private String content;
    private LocalDateTime createdAt;

    public static NotificationDTO fromNotification(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setReceiverId(notification.getReceiver().getId());
        dto.setType(notification.getType());
        dto.setContent(notification.getContent());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
