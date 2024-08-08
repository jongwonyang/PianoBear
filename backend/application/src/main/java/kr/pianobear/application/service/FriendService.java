package kr.pianobear.application.service;

import kr.pianobear.application.model.FriendRequest;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.FriendRequestRepository;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.service.NotificationService;
import kr.pianobear.application.controller.NotificationController;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    // 수락 및 거절 메서드
    public void acceptFriendRequest(Long requestId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Optional<FriendRequest> requestOpt = friendRequestRepository.findById(requestId);

        if (requestOpt.isPresent()) {
            FriendRequest friendRequest = requestOpt.get();
            if (!friendRequest.getReceiver().getId().equals(currentUserId))
                return;
            Member sender = friendRequest.getSender();
            Member receiver = friendRequest.getReceiver();
            sender.addFriend(receiver);
            memberRepository.save(sender);
            friendRequestRepository.delete(friendRequest);

            // 실시간 알림 전송
            notificationController.sendNotificationToClients(String.format("%s 님과 친구가 되었습니다!!", sender.getName()));
        }
    }

    public void rejectFriendRequest(Long requestId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Optional<FriendRequest> requestOpt = friendRequestRepository.findById(requestId);

        if (requestOpt.isPresent()) {
            FriendRequest friendRequest = requestOpt.get();
            if (!friendRequest.getReceiver().getId().equals(currentUserId))
                return;
            friendRequestRepository.delete(friendRequest);

            // 실시간 알림 전송
//            notificationController.sendNotificationToClients("친구 추가 거절당했습니다..");
        }
    }
}
