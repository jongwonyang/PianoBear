package kr.pianobear.application.controller;

import kr.pianobear.application.dto.ChatRoomDTO;
import kr.pianobear.application.dto.MessageDTO;
import kr.pianobear.application.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 1. 친구와의 채팅방으로 이동 - 과거 메시지 포함
    @GetMapping("/room/{friendId}")
    public ChatRoomDTO enterChatRoom(@PathVariable String friendId) {
        String currentUserId = getCurrentUserId(); // 현재 로그인된 사용자 ID를 가져오는 메서드
        return chatRoomService.getOrCreateChatRoom(currentUserId, friendId);
    }

    // 2. 메시지 보내기 - WebSocket 사용
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        // 메시지를 DB에 저장하고, 클라이언트에게 보냅니다.
        MessageDTO savedMessage = chatRoomService.sendMessage(
                messageDTO.getSenderId(),
                messageDTO.getReceiverId(),
                messageDTO.getContent(),
                messageDTO.getAttachedFileUrl() != null ? getFileIdFromUrl(messageDTO.getAttachedFileUrl()) : null
        );

        // 메시지를 해당 채팅방의 모든 사용자에게 전송
        messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getChatRoomId(), savedMessage);

        return savedMessage;
    }

    // 클라이언트로부터 메시지 받기 및 처리
    @MessageMapping("/chat/{roomId}")
    public void processMessageFromClient(MessageDTO messageDTO) {
        MessageDTO savedMessage = chatRoomService.sendMessage(
                messageDTO.getSenderId(),
                messageDTO.getReceiverId(),
                messageDTO.getContent(),
                messageDTO.getAttachedFileUrl() != null ? getFileIdFromUrl(messageDTO.getAttachedFileUrl()) : null
        );

        // 메시지를 해당 채팅방의 모든 사용자에게 전송
        messagingTemplate.convertAndSend("/topic/chat/" + messageDTO.getChatRoomId(), savedMessage);
    }

    private String getCurrentUserId() {
        // SecurityContextHolder 등을 이용해 현재 로그인한 사용자의 ID를 반환하는 로직
        // 예: return SecurityUtil.getCurrentUserId();
        return "currentUserId"; // 실제 구현 필요
    }

    private Long getFileIdFromUrl(String url) {
        // URL에서 파일 ID를 추출하는 로직을 구현
        // 예: return Long.parseLong(url.substring(url.lastIndexOf("/") + 1));
        return null; // 실제 구현 필요
    }
}
