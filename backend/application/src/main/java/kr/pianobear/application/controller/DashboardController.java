package kr.pianobear.application.controller;

import kr.pianobear.application.dto.DashboardSummaryDTO;
import kr.pianobear.application.service.DashboardService;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<DashboardSummaryDTO> dashboardSummary() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Optional<DashboardSummaryDTO> summary = dashboardService.getSummary(currentUserId);

        return ResponseEntity.ok(null);
    }
}
