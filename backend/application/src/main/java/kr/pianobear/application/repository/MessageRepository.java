package kr.pianobear.application.repository;

import kr.pianobear.application.model.ChatRoom;
import kr.pianobear.application.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoom(ChatRoom chatRoom);
}
