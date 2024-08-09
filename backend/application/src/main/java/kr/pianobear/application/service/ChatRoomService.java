package kr.pianobear.application.service;

import kr.pianobear.application.dto.MessageDTO;
import kr.pianobear.application.model.ChatRoom;
import kr.pianobear.application.model.FileData;
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

    @Autowired
    private FileDataService fileDataService;

    @Transactional
    public MessageDTO sendMessage(String senderId, String receiverId, String content, Long fileId) {
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
            message.setChatRoom(chatRoom);  // 메시지가 속한 채팅방 설정
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(content);
            message.setTimestamp(LocalDateTime.now());

            // 파일 첨부 처리
//            if (fileId != null) {
//                Optional<FileData> fileDataOpt = fileDataService.getFileById(fileId);
//                fileDataOpt.ifPresent(message::setAttachedFile);
//            }

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


    // Message 엔티티를 MessageDTO로 변환하는 메서드
    private MessageDTO convertToDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setChatRoomId(message.getChatRoom().getId());  // 채팅방 ID 설정
        messageDTO.setSenderId(message.getSender().getId());
        messageDTO.setReceiverId(message.getReceiver().getId());
        messageDTO.setContent(message.getContent());
        messageDTO.setTimestamp(message.getTimestamp().toString());

        // 첨부 파일 URL 추가
        if (message.getAttachedFile() != null) {
            messageDTO.setAttachedFileUrl(FileDataService.getDownloadPath(message.getAttachedFile()).orElse(null));
        }

        return messageDTO;
    }
}
