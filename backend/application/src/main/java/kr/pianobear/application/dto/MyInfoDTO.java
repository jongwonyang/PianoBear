package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "내 정보 조회")
public class MyInfoDTO {

    public MyInfoDTO(String id,
                     String email,
                     String name,
                     Character gender,
                     LocalDate birthday,
                     String profilePic,
                     String statusMessage,
                     Boolean authEmail,
                     String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.profilePic = profilePic;
        this.statusMessage = statusMessage;
        this.authEmail = authEmail;
        this.role = role;
    }

    @Schema(description = "로그인 아이디")
    private String id;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "성별")
    private Character gender;

    @Schema(description = "생년월일")
    private LocalDate birthday;

    @Schema(description = "프로필 사진 URL")
    private String profilePic;

    @Schema(description = "상태 메시지")
    private String statusMessage;

    @Schema(description = "이메일 인증 여부")
    private Boolean authEmail;

    @Schema(description = "권한 (ROLE_GUEST: 준회원, ROLE_MEMBER: 정회원)")
    private String role;
}
