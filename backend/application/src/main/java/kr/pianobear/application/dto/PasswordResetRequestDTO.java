package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "비밀번호 초기화 요청 DTO")
public class PasswordResetRequestDTO {

    @Schema(description = "로그인 아이디")
    private String id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "이메일")
    private String email;
}
