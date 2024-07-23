package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.RegisterRequestDTO;
import kr.pianobear.application.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "회원 및 인증 API")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "회원 가입")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequestDTO registerRequestDTO,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic) {
//        authService.register(registerRequestDTO, profilePic);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
