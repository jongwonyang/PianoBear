package kr.pianobear.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/search/{userId}")
    @Operation(summary = "사용자 ID로 친구 정보 반환")
    public FriendDTO getFriendById(@PathVariable String userId) {
        return friendService.getFriendById(userId);
    }

    @GetMapping("/requests")
    @Operation(summary = "로그인한 계정의 친구 요청 목록 반환")
    public List<FriendRequest> getPendingFriendRequests() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        return friendService.getPendingFriendRequests(currentUserId);
    }

    @PostMapping("/requests/send/{receiverId}")
    @Operation(summary = "지정한 사용자를 대상으로 하는 친구 요청 생성")
    public void sendFriendRequest(@PathVariable String receiverId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        friendService.sendFriendRequest(currentUserId, receiverId);
    }

    @PostMapping("/requests/{requestId}/accept")
    @Operation(summary = "지정한 친구 요청 수락")
    public void acceptFriendRequest(@PathVariable Long requestId) {
        friendService.acceptFriendRequest(requestId);
    }

    @PostMapping("/requests/{requestId}/reject")
    @Operation(summary = "지정한 친구 요청 거절")
    public void rejectFriendRequest(@PathVariable Long requestId) {
        friendService.rejectFriendRequest(requestId);
    }

}
