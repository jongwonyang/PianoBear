package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "음악 요약 DTO")
public class MusicSummaryDTO {

    @Schema(description = "악보 썸네일")
    private String musicImg;

    @Schema(description = "악보 제목")
    private String title;

    @Schema(description = "악보 작곡가")
    private String artist;

    @Schema(description = "악보 찜 여부")
    private Boolean favorite;
}
