package kr.pianobear.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.FriendDTO;
import kr.pianobear.application.model.FriendRequest;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.service.FriendService;
import kr.pianobear.application.util.SecurityUtil;

@RestController
@RequestMapping("/api/v1/friends")
@Tag(name = "Friend", description = "친구 관련 API")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @GetMapping("/")
    @Operation(summary = "로그인한 계정의 친구목록 반환")
    public List<FriendDTO> getFriends() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        return friendService.getFriends(currentUserId);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "사용자 ID로 친구 삭제")
    public void deleteFriendById(@PathVariable String userId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        friendService.deleteFriendById(currentUserId, userId);
    }

    @GetMapping("/search/{userId}")
    @Operation(summary = "사용자 ID로 친구 정보 반환")
    public FriendDTO getFriendById(@PathVariable String userId) {
        return friendService.getFriendById(userId);
    }

    @GetMapping("/requests/received")
    @Operation(summary = "로그인한 계정에게 친구 요청한 계정 반환")
    public List<FriendDTO> getReceivedFriendRequest() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        return friendService.getReceivedFriendRequests(currentUserId);
    }

    @GetMapping("/requests/sent")
    @Operation(summary = "로그인한 계정에서 친구 요청한 상대방 계정 목록 반환")
    public List<FriendDTO> getSentFriendRequest() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        return friendService.getSentFriendRequests(currentUserId);
    }

    @GetMapping("/requests/is-sent/{receiverId}")
    @Operation(summary = "로그인한 계정이 대상 사용자에게 요청했는지 여부 반환")
    public boolean getPendingFriendRequests(@PathVariable String receiverId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        return friendService.isExistPendingFriendRequests(currentUserId, receiverId);
    }

    @PostMapping("/requests/send/{receiverId}")
    @Operation(summary = "지정한 사용자를 대상으로 하는 친구 요청 생성")
    public void sendFriendRequest(@PathVariable String receiverId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        friendService.sendFriendRequest(currentUserId, receiverId);
    }

    @PostMapping("/requests/received/{senderId}/accept")
    @Operation(summary = "지정한 친구 요청 수락")
    public void acceptFriendRequest(@PathVariable String senderId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        friendService.acceptFriendRequest(senderId, currentUserId);
    }

    @PostMapping("/requests/received/{senderId}/reject")
    @Operation(summary = "지정한 친구 요청 거절")
    public void rejectFriendRequest(@PathVariable String senderId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        friendService.rejectFriendRequest(senderId, currentUserId);
    }

}
