package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.NotificationDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.service.NotificationService;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification", description = "Notification management API")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MemberRepository memberRepository;

    @Operation(summary = "알림 불러오기", description = "받는 사용자의 알림 목록을 불러온다")
    @GetMapping("/")
    public List<NotificationDTO> getNotifications() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Member receiver = new Member(currentUserId);
        return notificationService.getNotifications(receiver);
    }

    @Operation(summary = "알림 개수 불러오기", description = "보유 알림 개수를 불러옴으로서 개수 저장가능")
    @GetMapping("/count")
    public long getNotificationCount() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Member receiver = new Member(currentUserId);
        return notificationService.getNotificationCount(receiver);
    }

    @Operation(summary = "특정 알림 삭제", description = "거절함으로서 알림 삭제 가능")
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    @Operation(summary = "전체 알림 삭제", description = "전체 알림 삭제가 가능함")
    @DeleteMapping("/clear")
    public void clearNotifications() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Member receiver = new Member(currentUserId);
        notificationService.clearNotifications(receiver);
    }
}
