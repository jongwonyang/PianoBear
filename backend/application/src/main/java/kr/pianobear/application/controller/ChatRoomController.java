package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.ChatRoomDTO;
import kr.pianobear.application.dto.MessageDTO;
import kr.pianobear.application.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Chat", description = "채팅 API")
@RequestMapping("/api/v1/ws")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 1. 친구와의 채팅방으로 이동 - 과거 메시지 포함
    @Operation(summary = "채팅방 열기(과거 메시지 포함)")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/room/{friendId}")
    public ChatRoomDTO enterChatRoom(@PathVariable String friendId) {
        return chatRoomService.getOrCreateChatRoom(friendId);
    }

    // 2. 메시지 보내기 - WebSocket 사용
    @Operation(summary = "채팅 메시지 보내기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @MessageMapping("/sendMessage")
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        // 메시지를 DB에 저장하고, 클라이언트에게 보냅니다.
        MessageDTO savedMessage = chatRoomService.sendMessage(
                messageDTO.getReceiverId(),
                messageDTO.getContent()
        );

        // 메시지를 해당 채팅방의 모든 사용자에게 전송
        messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getChatRoomId(), savedMessage);

        return savedMessage;
    }

    // 클라이언트로부터 메시지 받기 및 처리
    @Operation(summary = "메시지 받기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @MessageMapping("/chat/{roomId}")
    public void processMessageFromClient(MessageDTO messageDTO) {
        MessageDTO savedMessage = chatRoomService.sendMessage(
                messageDTO.getReceiverId(),
                messageDTO.getContent()
        );

        // 메시지를 해당 채팅방의 모든 사용자에게 전송
        messagingTemplate.convertAndSend("/topic/chat/" + messageDTO.getChatRoomId(), savedMessage);
    }
}
