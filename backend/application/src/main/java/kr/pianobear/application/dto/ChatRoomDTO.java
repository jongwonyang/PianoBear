package kr.pianobear.application.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatRoomDTO {
    private Long id;
    private String member1Id;
    private String member2Id;
    private List<MessageDTO> messages;
}

