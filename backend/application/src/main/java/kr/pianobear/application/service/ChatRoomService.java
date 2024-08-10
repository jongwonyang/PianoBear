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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public ChatRoomDTO getOrCreateChatRoom(String friendId) {
        String memberId = SecurityUtil.getCurrentUserId();  // 현재 로그인된 사용자 ID 가져오기
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        Optional<Member> friendOpt = memberRepository.findById(friendId);

        if (memberOpt.isPresent() && friendOpt.isPresent()) {
            Member member = memberOpt.get();
            Member friend = friendOpt.get();

            // 채팅방 조회 (member1, member2 순서 또는 반대 순서로 검색)
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByMember1AndMember2(member, friend);
            if (!chatRoomOpt.isPresent()) {
                chatRoomOpt = chatRoomRepository.findByMember2AndMember1(member, friend);
            }

            ChatRoom chatRoom = chatRoomOpt.orElseGet(() -> createChatRoom(member, friend));

            return convertToChatRoomDTO(chatRoom);
        } else {
            throw new RuntimeException("Member or Friend not found");
        }
    }

    public MessageDTO sendMessage(String receiverId, String content) {
        String senderId = SecurityUtil.getCurrentUserId();  // 현재 로그인된 사용자 ID 가져오기
        Optional<Member> senderOpt = memberRepository.findById(senderId);
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            Member sender = senderOpt.get();
            Member receiver = receiverOpt.get();

            // 채팅방 조회 혹은 생성
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByMember1AndMember2(sender, receiver);
            if (!chatRoomOpt.isPresent()) {
                chatRoomOpt = chatRoomRepository.findByMember2AndMember1(sender, receiver);
            }

            ChatRoom chatRoom = chatRoomOpt.orElseGet(() -> createChatRoom(sender, receiver));

            // 메시지 생성 및 저장
            Message message = new Message();
            message.setChatRoom(chatRoom);  // 메시지가 속한 채팅방 설정
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(content);
            message.setTimestamp(LocalDateTime.now());

            messageRepository.save(message);

            // 메시지 DTO로 변환
            return convertToDTO(message);
        } else {
            throw new RuntimeException("Sender or Receiver not found");
        }
    }

    private ChatRoom createChatRoom(Member member1, Member member2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setMember1(member1);
        chatRoom.setMember2(member2);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    private ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom) {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setId(chatRoom.getId());
        chatRoomDTO.setMember1Id(chatRoom.getMember1().getId());
        chatRoomDTO.setMember2Id(chatRoom.getMember2().getId());

        List<MessageDTO> messages = chatRoom.getMessages().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        chatRoomDTO.setMessages(messages);

        return chatRoomDTO;
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setChatRoomId(message.getChatRoom().getId());  // 채팅방 ID 설정
        messageDTO.setSenderId(message.getSender().getId());
        messageDTO.setReceiverId(message.getReceiver().getId());
        messageDTO.setContent(message.getContent());
        messageDTO.setTimestamp(message.getTimestamp().toString());

        return messageDTO;
    }
}
