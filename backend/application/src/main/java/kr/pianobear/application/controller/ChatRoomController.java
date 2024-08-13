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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@Tag(name = "Chat", description = "채팅 API")
@RequestMapping("/api/v1/ws")
public class ChatRoomController {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomController.class);

    // 의존성 주입: ChatRoomService와 SimpMessagingTemplate을 주입
    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 1. 친구와의 채팅방으로 이동 - 과거 메시지 포함
    @Operation(summary = "채팅방 열기(과거 메시지 포함)")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/room/{friendId}")
    public ChatRoomDTO enterChatRoom(@PathVariable String friendId) {
        // 친구와의 채팅방을 생성하거나 가져온 뒤, 해당 방의 정보를 반환
        return chatRoomService.getOrCreateChatRoom(friendId);
    }

    // 2. 메시지 보내기 - WebSocket 사용
    @Operation(summary = "채팅 메시지 보내기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @MessageMapping("/sendMessage")
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        // Logger를 사용하여 메시지 정보를 출력
        logger.debug("Received message from sender: {}, to receiver: {}, content: {}",
                messageDTO.getSenderId(), messageDTO.getReceiverId(), messageDTO.getContent());

        // 클라이언트에서 전송된 메시지를 DB에 저장하고, 저장된 메시지 정보를 가져옴
        MessageDTO savedMessage = chatRoomService.sendMessage(
                messageDTO.getReceiverId(),
                messageDTO.getContent()
        );

        // 저장된 메시지를 해당 채팅방에 연결된 모든 클라이언트에게 전송
        messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getChatRoomId(), savedMessage);

        // Logger를 사용하여 저장된 메시지 정보를 출력
        logger.debug("Message saved with chatRoomId: {}, senderId: {}, content: {}",
                savedMessage.getChatRoomId(), savedMessage.getSenderId(), savedMessage.getContent());

        return savedMessage;  // 저장된 메시지 DTO를 반환
    }

    // 클라이언트로부터 메시지 받기 및 처리
    @Operation(summary = "메시지 받기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @MessageMapping("/chat/{roomId}")
    public void processMessageFromClient(MessageDTO messageDTO) {
        // 클라이언트로부터 받은 메시지를 DB에 저장하고, 저장된 메시지 정보를 가져옴
        MessageDTO savedMessage = chatRoomService.sendMessage(
                messageDTO.getReceiverId(),
                messageDTO.getContent()
        );

        // 저장된 메시지를 해당 채팅방에 연결된 모든 클라이언트에게 전송
        messagingTemplate.convertAndSend("/topic/chat/" + messageDTO.getChatRoomId(), savedMessage);
    }
}
