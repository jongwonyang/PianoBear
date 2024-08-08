package kr.pianobear.application.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    private String type; // "FRIEND_REQUEST", "CHAT", "PLAYGROUND"
    private String content;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public Notification(Member receiver, String type, String content) {
        this();
        this.receiver = receiver;
        this.type = type;
        this.content = content;
    }
}
