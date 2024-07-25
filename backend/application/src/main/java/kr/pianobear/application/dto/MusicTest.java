package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@Schema(description = "연주 도전 DTO")
public class MusicTest {

    @NotBlank
    @Schema(description = "도전 아이디")
    private int id;

    @NotBlank
    @Schema(description = "도전 점수")
    private int grade;

    @NotBlank
    @Schema(description = "사용자 아이디")
    private String userId;

    @NotBlank
    @Schema(description = "악보 아이디")
    private int musicId;
}
