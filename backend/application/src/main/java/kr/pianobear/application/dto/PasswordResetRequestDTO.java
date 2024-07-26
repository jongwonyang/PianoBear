package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "비밀번호 초기화 요청 DTO")
public class PasswordResetRequestDTO {

    @Schema(description = "기존 비밀번호")
    private String oldPassword;

    @Schema(description = "새로운 비밀번호")
    private String newPassword;
}
