package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "대시보드 - 요약 섹션 응답 DTO")
public class DashboardSummaryDTO {

    @Schema(description = "사용자 아이디")
    private String userId;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "프로필 이미지 경로")
    private String profileImage;

    @Schema(description = "스트릭 (연속 연습일)")
    private int streak;

    @Schema(description = "최애곡 (순서대로 1, 2, 3위)")
    private List<String> most;
}
