package kr.pianobear.application.repository;

import kr.pianobear.application.model.FriendRequest;
import kr.pianobear.application.model.Member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface FriendRequestRepository extends CrudRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySenderAndReceiver(Member sender, Member receiver);

    List<FriendRequest> findByReceiver(Member receiver);

    List<FriendRequest> findBySender(Member receiver);

    boolean existsBySenderAndReceiver(Member sender, Member receiver);
    // boolean existsBySenderAndReceiver();
}
