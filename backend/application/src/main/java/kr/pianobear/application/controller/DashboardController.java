package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.DashboardSummaryDTO;
import kr.pianobear.application.dto.FriendDTO;
import kr.pianobear.application.model.MusicPractice;
import kr.pianobear.application.service.DashboardService;
import kr.pianobear.application.service.MusicPracticeService;
import kr.pianobear.application.service.UserService;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "메인 대시보드 API")
public class DashboardController {

    private final DashboardService dashboardService;
    private final MusicPracticeService musicPracticeService;
    private final UserService userService;

    @Autowired
    public DashboardController(DashboardService dashboardService, MusicPracticeService musicPracticeService, UserService userService) {
        this.dashboardService = dashboardService;
        this.musicPracticeService = musicPracticeService;
        this.userService = userService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @Operation(summary = "1번 섹션 (상단) - 요약 정보")
    public ResponseEntity<DashboardSummaryDTO> dashboardSummary() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Optional<DashboardSummaryDTO> summary = dashboardService.getSummary(currentUserId);

        if (summary.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(summary.get());
    }

    @GetMapping("/practice-records")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @Operation(summary = "2번 섹션 (좌측) - 월별 연습 기록")
    public ResponseEntity<List<MusicPractice>> practiceRecords(@RequestParam int year, @RequestParam int month) {
        String userId = SecurityUtil.getCurrentUserId();

        List<MusicPractice> records = musicPracticeService.getMonthlyPracticeRecords(userId, year, month);

        return ResponseEntity.ok(records);
    }

    @GetMapping("/online-friends")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @Operation(summary = "3번 섹션 (우측) - 온라인 친구 목록")
    public ResponseEntity<List<FriendDTO>> getOnlineFriends() {
        String userId = SecurityUtil.getCurrentUserId();
        List<FriendDTO> onlineFriends = userService.getOnlineFriends(userId).orElse(new ArrayList<>());

        return ResponseEntity.status(HttpStatus.OK).body(onlineFriends);
    }

}
