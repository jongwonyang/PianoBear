package kr.pianobear.application.service;


import kr.pianobear.application.controller.NotificationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.pianobear.application.dto.FriendDTO;
import kr.pianobear.application.model.FriendRequest;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.FriendRequestRepository;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.util.SecurityUtil;

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

    @Autowired
    private NotificationController notificationController;

    public void sendFriendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId))
            return;

        Optional<Member> senderOpt = memberRepository.findById(senderId);
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            Member sender = senderOpt.get();
            Member receiver = receiverOpt.get();
            FriendRequest friendRequest = new FriendRequest(sender, receiver);
            friendRequestRepository.save(friendRequest);

            // 알림 생성
            String content = String.format("{\"senderId\":\"%s\", \"senderName\":\"%s\", \"senderProfilePic\":\"%s\"}",
                    sender.getId(), sender.getName(), sender.getProfilePic() != null ? sender.getProfilePic().getFilePath() : "");
            notificationService.createNotification(receiver, "FRIEND_REQUEST", content);

            // 실시간 알림 전송
            notificationController.sendNotificationToClients(content);
        }
    }

    public void acceptFriendRequest(Long requestId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Optional<FriendRequest> requestOpt = friendRequestRepository.findById(requestId);

        if (requestOpt.isPresent()) {
            FriendRequest friendRequest = requestOpt.get();
            if (friendRequest.getReceiver().getId().equals(currentUserId))
                return;
            Member sender = friendRequest.getSender();
            Member receiver = friendRequest.getReceiver();
            sender.addFriend(receiver);
            memberRepository.save(sender);
            friendRequestRepository.delete(friendRequest);
        }
    }

    public void rejectFriendRequest(Long requestId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Optional<FriendRequest> requestOpt = friendRequestRepository.findById(requestId);

        if (requestOpt.isPresent()) {
            FriendRequest friendRequest = requestOpt.get();
            if (friendRequest.getReceiver().getId().equals(currentUserId))
                return;
            friendRequestRepository.delete(friendRequest);
        }
    }

    public List<FriendRequest> getPendingFriendRequests(String userId) {
        Optional<Member> userOpt = memberRepository.findById(userId);
        if (userOpt.isPresent()) {
            return friendRequestRepository.findByReceiver(userOpt.get());
        }
        return List.of();
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
}
