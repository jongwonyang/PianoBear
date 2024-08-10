package kr.pianobear.application.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.pianobear.application.model.ChatRoom;
import kr.pianobear.application.model.Member;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByMember1AndMember2(Member member1, Member member2);

    Optional<ChatRoom> findByMember2AndMember1(Member member2, Member member1);
}
