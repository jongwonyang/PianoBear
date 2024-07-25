package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import kr.pianobear.application.dto.LoginRequestDTO;
import kr.pianobear.application.dto.LoginResponseDTO;
import kr.pianobear.application.dto.RegisterRequestDTO;
import kr.pianobear.application.dto.TokenRefreshRequestDTO;
import kr.pianobear.application.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
    @Operation(summary = "회원 가입")
    public ResponseEntity<Void> register(
            @RequestPart(name = "registerRequestDTO") RegisterRequestDTO registerRequestDTO,
            @RequestPart(name = "profilePic") MultipartFile profilePic) {
        try {
            authService.register(registerRequestDTO, profilePic);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
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

    @GetMapping("/check-user-id")
    @Operation(summary = "아이디 중복 검사")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        boolean exists = authService.isUserIdExists(userId);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<LoginResponseDTO> result = authService.login(loginRequestDTO.getId(), loginRequestDTO.getPassword());

        if (result.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return ResponseEntity.ok(result.get());
    }

    @GetMapping("/test")
    @Operation(summary = "test")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("YOU ARE AUTHORIZED!");
    }

    @PostMapping("/refresh")
    public void refresh(@RequestBody TokenRefreshRequestDTO tokenRefreshRequestDTO) {

    }
}
