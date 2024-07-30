package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDTO {

    @NotBlank
    @Schema(description = "로그인 아이디")
    private String id;

    @NotBlank
    @Schema(description = "로그인 비밀번호")
    private String password;
}
