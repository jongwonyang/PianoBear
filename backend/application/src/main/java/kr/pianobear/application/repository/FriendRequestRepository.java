package kr.pianobear.application.repository;

import kr.pianobear.application.model.FriendRequest;
import kr.pianobear.application.model.Member;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface FriendRequestRepository extends CrudRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiver(Member receiver);
    boolean exexistsBySenderAndReceiver();
}
