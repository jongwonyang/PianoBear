package kr.pianobear.application.repository;

import kr.pianobear.application.model.ChatRoom;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByMembers(Member member1, Member member2);
}

