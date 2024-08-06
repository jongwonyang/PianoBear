package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.pianobear.application.model.Member;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @Schema(description = "악보 mxl 경로")
    private String musicXmlRoute;

    @NotBlank
    @Schema(description = "변환된 악보 경로")
    private String modifiedMusicXmlRoute;

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
    private LocalDate uploadDate;

    @NotBlank
    @Schema(description = "악보 작곡가")
    private String artist;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}
