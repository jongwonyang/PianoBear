package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.ExistsResponseDTO;
import kr.pianobear.application.dto.MyInfoDTO;
import kr.pianobear.application.service.UserService;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "회원 정보 API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check-user-id")
    @Operation(summary = "아이디 중복 검사 (구현 완료)")
    public ResponseEntity<ExistsResponseDTO> checkUserId(@RequestParam String userId) {
        boolean exists = userService.userIdExists(userId);
        return ResponseEntity.ok(new ExistsResponseDTO(exists));
    }

    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복 검사 (구현 완료)")
    public ResponseEntity<ExistsResponseDTO> checkEmail(@RequestParam String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(new ExistsResponseDTO(exists));
    }

    @GetMapping("/my-info")
    @Operation(summary = "내 정보 조회")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<MyInfoDTO> myInfo() {
        String currentUserId = SecurityUtil.getCurrentUserId();

        Optional<MyInfoDTO> myInfo = userService.getMyInfo(currentUserId);

        if (myInfo.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(myInfo.get());
    }
}
