package kr.pianobear.application.service;

import kr.pianobear.application.dto.FriendDTO;
import kr.pianobear.application.model.FriendRequest;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.FriendRequestRepository;
import kr.pianobear.application.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private NotificationService notificationService;

    public void sendFriendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId)) {
            return;
        }

        Optional<Member> senderOpt = memberRepository.findById(senderId);
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            Member sender = senderOpt.get();
            Member receiver = receiverOpt.get();

            Optional<FriendRequest> requestOpt = friendRequestRepository
                    .findBySenderAndReceiver(sender, receiver);
            if (requestOpt.isPresent()) {
                return;
            }

            FriendRequest friendRequest = new FriendRequest(sender, receiver);
            friendRequestRepository.save(friendRequest);

            // 알림 생성 및 전송
            String content = String.format("{\"senderId\":\"%s\", \"senderName\":\"%s\", \"senderProfilePic\":\"%s\"}",
                    sender.getId(), sender.getName(),
                    sender.getProfilePic() != null ? sender.getProfilePic().getFilePath() : "");
            notificationService.createNotification(receiver, "FRIEND_REQUEST", content);
        }
    }

    public void acceptFriendRequest(String senderId, String receiverId) {
        Optional<Member> senderOpt = memberRepository.findById(senderId);
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            Member sender = senderOpt.get();
            Member receiver = receiverOpt.get();

            Optional<FriendRequest> requestOpt = friendRequestRepository
                    .findBySenderAndReceiver(sender, receiver);

            if (requestOpt.isPresent()) {
                FriendRequest friendRequest = requestOpt.get();
                sender.addFriend(receiver);
                memberRepository.save(sender);
                friendRequestRepository.delete(friendRequest);

                // 알림 생성 및 전송
                String content = String.format("%s 님과 친구가 되었습니다!!", sender.getName());
                notificationService.createNotification(receiver, "FRIEND_ACCEPTED", content);
            }
        }
    }

    public void rejectFriendRequest(String senderId, String receiverId) {
        Optional<Member> senderOpt = memberRepository.findById(senderId);
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            Member sender = senderOpt.get();
            Member receiver = receiverOpt.get();

            Optional<FriendRequest> requestOpt = friendRequestRepository
                    .findBySenderAndReceiver(sender, receiver);

            if (requestOpt.isPresent()) {
                FriendRequest friendRequest = requestOpt.get();
                friendRequestRepository.delete(friendRequest);

                // 알림 생성 및 전송
                String content = "친구 추가 요청이 거절되었습니다.";
                notificationService.createNotification(friendRequest.getSender(), "FRIEND_REJECTED", content);
            }
        }
    }

    public List<FriendDTO> getSentFriendRequests(String senderId) {
        Optional<Member> senderOpt = memberRepository.findById(senderId);
        if (senderOpt.isPresent()) {
            List<FriendRequest> requestList = friendRequestRepository.findBySender(senderOpt.get());

            return requestList.stream().map((request) -> {
                return FriendDTO.fromMember(request.getReceiver());
            }).toList();
        }
        return List.of();
    }

    public List<FriendDTO> getReceivedFriendRequests(String receiverId) {
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);
        if (receiverOpt.isPresent()) {
            List<FriendRequest> requestList = friendRequestRepository.findByReceiver(receiverOpt.get());

            return requestList.stream().map((request) -> {
                return FriendDTO.fromMember(request.getSender());
            }).toList();
        }
        return List.of();
    }

    public boolean isExistPendingFriendRequests(String senderId, String receiverId) {
        Optional<Member> senderOpt = memberRepository.findById(senderId);
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            Member sender = senderOpt.get();
            Member receiver = receiverOpt.get();

            Optional<FriendRequest> requestOpt = friendRequestRepository
                    .findBySenderAndReceiver(sender, receiver);

            return requestOpt.isPresent();
        } else {
            throw new RuntimeException("Sender or Receiver not found");
        }
    }

    public List<FriendDTO> getFriends(String memberId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            return member.getFriends().stream().map(FriendDTO::fromMember).collect(Collectors.toList());
        } else {
            throw new RuntimeException("Member not found");
        }
    }

    public FriendDTO getFriendById(String userId) {
        Optional<Member> memberOpt = memberRepository.findById(userId);

        if (memberOpt.isPresent()) {
            return FriendDTO.fromMember(memberOpt.get());
        } else {
            throw new RuntimeException("Member not found");
        }
    }

    public void deleteFriendById(String currentUserId, String targetId) {

        Optional<Member> currentUserOpt = memberRepository.findById(currentUserId);
        Optional<Member> targetOpt = memberRepository.findById(targetId);

        if (currentUserOpt.isPresent() && targetOpt.isPresent()) {
            Member currentUser = currentUserOpt.get();
            Member target = targetOpt.get();

            currentUser.removeFriend(target);
            memberRepository.save(currentUser);
        }
    }
}
