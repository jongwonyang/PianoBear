package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "중복 검사 결과")
@AllArgsConstructor
public class ExistsResponseDTO {

    @Schema(description = "존재 여부")
    private boolean exists;
}
