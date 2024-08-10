package kr.pianobear.application.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.pianobear.application.model.ChatRoom;
import kr.pianobear.application.model.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c FROM ChatRoom c WHERE (c.member1 = :member1 AND c.member2 = :member2) OR (c.member1 = :member2 AND c.member2 = :member1)")
    Optional<ChatRoom> findByMemberIds(@Param("member1") Member member1, @Param("member2") Member member2);

}
