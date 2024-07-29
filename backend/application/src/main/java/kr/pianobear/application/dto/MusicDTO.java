package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@Schema(description = "음악 악보 DTO")
public class MusicDTO {

    @NotBlank
    @Schema(description = "악보 아이디")
    private int id;

    @NotBlank
    @Schema(description = "악보 제목")
    private String title;

    @NotBlank
    @Schema(description = "악보 원본 경로")
    private String originalFileRoute;

    @NotBlank
    @Schema(description = "변환된 악보 경로")
    private String changedFileRoute;

    @Schema(description = "악보별 연습량")
    private int practiceCount;

    @Schema(description = "가장 최근 연습한 날")
    private String recentPractice;

    @NotBlank
    @Schema(description = "사용자 아이디")
    private String userId;

    @Schema(description = "악보 썸네일")
    private String musicImg;

    @NotBlank
    @Schema(description = "악보 찜 여부")
    private Boolean favorite;

    @NotBlank
    @Schema(description = "악보 업로드 날짜")
    private String uploadDate;
}
