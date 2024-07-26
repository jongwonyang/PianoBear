package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "비밀번호 초기화 요청 DTO")
public class PasswordResetRequestDTO {

    private String id;
    private String name;
    private String email;
}
