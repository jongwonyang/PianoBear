package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import kr.pianobear.application.dto.*;
import kr.pianobear.application.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "회원 및 인증 API")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원 가입 (구현 완료)")
    public ResponseEntity<RegisterResponseDTO> register(
            @RequestPart(name = "registerRequestDTO") RegisterRequestDTO registerRequestDTO,
            @RequestPart(name = "profilePic", required = false) MultipartFile profilePic) {
        RegisterResponseDTO response = new RegisterResponseDTO();

        try {
            authService.register(registerRequestDTO, profilePic);
            response.setSuccess(true);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DuplicateKeyException | IllegalArgumentException e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException | MessagingException e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/email-verification/{uuid}")
    public String emailVerification(@PathVariable String uuid, Model model) {
        model.addAttribute("uuid", uuid);
        return "email-verification";
    }

    @PostMapping("/email-verification")
    public ResponseEntity<Void> verifyEmail(@RequestBody Map<String, Object> map) {
        String uuid = (String) map.get("uuid");
        boolean result = authService.verifyEmail(uuid);

        if (result)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 (구현 완료)")
    public ResponseEntity<TokenPairDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResultDTO result = authService.login(loginRequestDTO.getId(), loginRequestDTO.getPassword());

        if (!result.isSuccess())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!result.isEmailVerified())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(result.getTokenPair());
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 리프레시 (구현 완료)")
    public ResponseEntity<TokenPairDTO> refresh(@RequestBody TokenRefreshRequestDTO tokenRefreshRequestDTO) {
        Optional<TokenPairDTO> result = authService.refresh(tokenRefreshRequestDTO.getRefreshToken());

        if (result.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(result.get());
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 (구현 완료)")
    public ResponseEntity<Void> logout(@RequestBody TokenPairDTO tokenPair) {
        String accessToken = tokenPair.getAccessToken();
        String refreshToken = tokenPair.getRefreshToken();

        authService.logout(accessToken, refreshToken);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset")
    @Operation(summary = "비밀번호 초기화 (구현 완료)")
    public ResponseEntity<Void> passwordReset(@RequestBody PasswordResetRequestDTO request) {
        try {
            boolean result = authService.resetPassword(request.getId(), request.getName(), request.getEmail());
            if (result)
                return ResponseEntity.ok().build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
