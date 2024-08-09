package kr.pianobear.application.service;

import kr.pianobear.application.dto.MessageDTO;
import kr.pianobear.application.model.ChatRoom;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Message;
import kr.pianobear.application.repository.ChatRoomRepository;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public MessageDTO sendMessage(String senderId, String receiverId, String content) {
        Optional<Member> senderOpt = memberRepository.findById(senderId);
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            Member sender = senderOpt.get();
            Member receiver = receiverOpt.get();

            // 채팅방 조회 혹은 생성
            ChatRoom chatRoom = chatRoomRepository.findByMembers(sender, receiver)
                    .orElseGet(() -> createChatRoom(sender, receiver));

            // 메시지 생성 및 저장
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(content);
            message.setTimestamp(LocalDateTime.now());

            messageRepository.save(message);

            // ChatRoom에도 메시지 추가
            chatRoom.getMessages().add(message);
            chatRoomRepository.save(chatRoom);

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

    private MessageDTO convertToDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setSenderId(message.getSender().getId());
        messageDTO.setReceiverId(message.getReceiver().getId());
        messageDTO.setContent(message.getContent());
        messageDTO.setTimestamp(message.getTimestamp().toString());
        return messageDTO;
    }
}
