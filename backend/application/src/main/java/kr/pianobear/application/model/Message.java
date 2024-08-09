package kr.pianobear.application.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private String content;
    private LocalDateTime timestamp;

    // 파일 첨부 기능
    @OneToOne(targetEntity = FileData.class)
    @Nullable
    private FileData attachedFile;
}

