package kr.pianobear.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.NotificationDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.service.NotificationService;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification", description = "Notification management API")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MemberRepository memberRepository;

    @Value("${jwt.access-expiration-time}")
    private int connectionTimeOut;

//    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter subscribe() {
//        String currentUserId = SecurityUtil.getCurrentUserId();
//        Member receiver = memberRepository.findById(currentUserId)
//                .orElseThrow(() -> new RuntimeException("Member not found")); // 데이터베이스에서 Member를 조회
//
//        SseEmitter emitter = notificationService.addEmitter(receiver, connectionTimeOut);
//
//        // 연결 시점에 밀린 알림을 전송합니다.
//        List<NotificationDTO> notifications = notificationService.getNotifications(receiver);
//        try {
//            for (NotificationDTO notification : notifications) {
//                emitter.send(SseEmitter.event().name("notification").data(notification));
//            }
//            emitter.send(SseEmitter.event()
//                    .name("connect")
//                    .data("connected!"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return emitter;
//    }

    @Operation(summary = "알림 받는 설정", description = "subscribe를 통해 알림을 받을 수 있게 된다")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        // 현재 사용자 ID를 가져옴
        String currentUserId = SecurityUtil.getCurrentUserId();

        // 디버깅용 로그 추가
        logger.info("Current User ID: {}", currentUserId);

        // null 체크
        if (currentUserId == null) {
            throw new RuntimeException("User ID cannot be null. User may not be authenticated.");
        }

        // 데이터베이스에서 Member 객체를 조회
        Member receiver = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Member not found for ID: " + currentUserId));

        // SseEmitter 설정 및 알림 전송
        SseEmitter emitter = notificationService.addEmitter(receiver, connectionTimeOut);

        // 연결 시점에 밀린 알림을 전송합니다.
        List<NotificationDTO> notifications = notificationService.getNotifications(receiver);
        try {
            for (NotificationDTO notification : notifications) {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            }
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            throw new RuntimeException("Error while sending notifications", e);
        }

        return emitter;
    }

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

    // 알림에 연결된 친구 요청도 삭제해야함
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
