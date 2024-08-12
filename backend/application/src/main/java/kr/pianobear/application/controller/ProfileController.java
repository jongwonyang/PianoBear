package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.NameUpdateRequestDTO;
import kr.pianobear.application.dto.PasswordUpdateRequestDTO;
import kr.pianobear.application.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "회원 정보 수정 API")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService){ this.profileService = profileService; }

    @PutMapping("/name")
    @Operation(summary = "이름 변경")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<String> updateName(@RequestBody NameUpdateRequestDTO request) {
        profileService.updateName(request.getId(), request.getNewName());
        return ResponseEntity.ok("이름이 성공적으로 변경되었습니다.");
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateRequestDTO request) {
        profileService.updatePassword(request.getId(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    @PutMapping("/photo")
    @Operation(summary = "프로필 사진 변경")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<String> updateProfilePhoto(@RequestPart String id, @RequestPart MultipartFile profilePic) {
        try {
            profileService.updatePhoto(id, profilePic);
            return ResponseEntity.ok("프로필 사진이 성공적으로 변경되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("프로필 사진 변경 중 오류가 발생했습니다.");
        }
    }
}
