package kr.pianobear.application.dto;

import lombok.Data;

@Data
public class LoginResultDTO {

    private boolean success;
    private boolean emailVerified;
    private TokenPairDTO tokenPair;

    public LoginResultDTO(boolean success, boolean emailVerified, TokenPairDTO tokenPair) {
        this.success = success;
        this.emailVerified = emailVerified;
        this.tokenPair = tokenPair;
    }
}
