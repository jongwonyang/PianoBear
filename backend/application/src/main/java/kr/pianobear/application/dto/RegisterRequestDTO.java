package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "회원가입 요청 DTO")
public class RegisterRequestDTO {

    @NotBlank
    @Size(min = 2, max = 20)
    @Schema(description = "로그인시 사용될 아이디")
    private String id;

    @NotBlank
    @Email
    @Schema(description = "인증할 이메일")
    private String email;

    @NotBlank
    @Schema(description = "실명")
    private String name;

    @NotBlank
    @Schema(description = "성별", allowableValues = {"M", "F"})
    private Character gender;

    @NotBlank
    @Schema(description = "생일")
    private LocalDate birthday;

    @NotBlank
    @Size(min = 2, max = 100)
    @Schema(description = "로그인시 사용될 비밀번호")
    private String password;

    @Schema(description = "프로필 사진")
    private String profilePic;
    private String statusMessage;
}
