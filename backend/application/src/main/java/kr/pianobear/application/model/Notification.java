package kr.pianobear.application.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    private String type; // "FRIEND_REQUEST", "CHAT", "PLAYGROUND" // "친구 추기" "채팅" "놀이터"
    private String content;
    private LocalDateTime createdAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToOne
    @JoinColumn(name = "friend_request_id", referencedColumnName = "id")
    private FriendRequest friendRequest;

    public Notification(Member receiver, String type, String content) {
        this();
        this.receiver = receiver;
        this.type = type;
        this.content = content;
    }
}
