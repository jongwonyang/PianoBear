package kr.pianobear.application.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;
}
