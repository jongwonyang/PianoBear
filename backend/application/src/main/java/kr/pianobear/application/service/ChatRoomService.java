package kr.pianobear.application.service;

import kr.pianobear.application.dto.ChatRoomDTO;
import kr.pianobear.application.dto.MessageDTO;
import kr.pianobear.application.model.ChatRoom;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Message;
import kr.pianobear.application.repository.ChatRoomRepository;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.repository.MessageRepository;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    // 의존성 주입: 각종 Repository를 주입하여 데이터베이스 접근
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NotificationService notificationService;

    // 트랜잭션 처리: getOrCreateChatRoom 메서드가 트랜잭션 내에서 실행됨
    @Transactional
    public ChatRoomDTO getOrCreateChatRoom(String friendId) {
        // 현재 로그인된 사용자의 ID를 가져옴
        String memberId = SecurityUtil.getCurrentUserId();

        // 현재 사용자와 친구의 Member 객체를 DB에서 조회
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        Optional<Member> friendOpt = memberRepository.findById(friendId);

        // 두 멤버가 모두 존재하는 경우에만 처리
        if (memberOpt.isPresent() && friendOpt.isPresent()) {
            Member member = memberOpt.get();
            Member friend = friendOpt.get();

            // 두 멤버가 참여한 채팅방을 한 번의 쿼리로 조회
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByMemberIds(member, friend);

            // 채팅방이 존재하면 그 채팅방을 사용, 없으면 새로 생성
            ChatRoom chatRoom = chatRoomOpt.orElseGet(() -> createChatRoom(member, friend));

            // 채팅방 엔티티를 DTO로 변환하여 반환
            return convertToChatRoomDTO(chatRoom);
        } else {
            // 만약 멤버나 친구를 찾지 못하면 예외 발생
            throw new RuntimeException("Member or Friend not found");
        }
    }

    public MessageDTO sendMessage(String receiverId, String content) {
        String senderId = SecurityUtil.getCurrentUserId();
        Optional<Member> senderOpt = memberRepository.findById(senderId);
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            Member sender = senderOpt.get();
            Member receiver = receiverOpt.get();

            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByMemberIds(sender, receiver);
            ChatRoom chatRoom = chatRoomOpt.orElseGet(() -> createChatRoom(sender, receiver));

            Message message = new Message();
            message.setChatRoom(chatRoom);
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(content);
            message.setTimestamp(LocalDateTime.now());

            chatRoom.getMessages().add(message);

            // 메시지를 DB에 저장
            Message savedMessage = messageRepository.save(message);

            // 알림 생성 및 전송 - JSON 형식으로 필요한 정보 포함
            String notificationContent = String.format(
                    "{\"senderId\":\"%s\", \"senderName\":\"%s\", \"senderProfilePic\":\"%s\", \"chatRoomId\":\"%d\"}",
                    sender.getId(), sender.getName(),
                    sender.getProfilePic() != null ? sender.getProfilePic().getFilePath() : "",
                    chatRoom.getId()
            );
            notificationService.createNotification(receiver, "CHAT", notificationContent);

            return convertToDTO(savedMessage);
        } else {
            throw new RuntimeException("Sender or Receiver not found");
        }
    }

    // 새 채팅방을 생성하는 메서드
    private ChatRoom createChatRoom(Member member1, Member member2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setMember1(member1);  // 첫 번째 멤버 설정
        chatRoom.setMember2(member2);  // 두 번째 멤버 설정
        chatRoomRepository.save(chatRoom);  // 채팅방을 DB에 저장
        return chatRoom;
    }

    // ChatRoom 엔티티를 ChatRoomDTO로 변환하는 메서드
    private ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom) {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setId(chatRoom.getId());  // 채팅방 ID 설정
        chatRoomDTO.setMember1Id(chatRoom.getMember1().getId());  // 첫 번째 멤버 ID 설정
        chatRoomDTO.setMember2Id(chatRoom.getMember2().getId());  // 두 번째 멤버 ID 설정

        // 채팅방의 모든 메시지를 DTO로 변환하여 리스트에 추가
        List<MessageDTO> messages = chatRoom.getMessages().stream()
                .sorted(Comparator.comparing(Message::getTimestamp))  // 시간순 정렬
                .map(this::convertToDTO)
                .collect(Collectors.toList());


        chatRoomDTO.setMessages(messages);  // 변환된 메시지 리스트를 DTO에 설정

        return chatRoomDTO;  // DTO 반환
    }

    // Message 엔티티를 MessageDTO로 변환하는 메서드
    private MessageDTO convertToDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());  // 메시지 ID 설정
        messageDTO.setChatRoomId(message.getChatRoom().getId());  // 채팅방 ID 설정
        messageDTO.setSenderId(message.getSender().getId());  // 송신자 ID 설정
        messageDTO.setReceiverId(message.getReceiver().getId());  // 수신자 ID 설정
        messageDTO.setContent(message.getContent());  // 메시지 내용 설정
        messageDTO.setTimestamp(message.getTimestamp().toString());  // 전송 시간 설정

        return messageDTO;  // DTO 반환
    }
}
