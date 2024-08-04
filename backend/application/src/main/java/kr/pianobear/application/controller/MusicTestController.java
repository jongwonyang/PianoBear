package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.MusicTestDTO;
import kr.pianobear.application.service.MusicTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/music/test")
@Tag(name = "Music Test API", description = "Music test management API")
public class MusicTestController {

    private final MusicTestService musicTestService;

    @Autowired
    public MusicTestController(MusicTestService musicTestService) {
        this.musicTestService = musicTestService;
    }

    @Operation(summary = "악보 도전", description = "도전 데이터 추가하기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/{id}")
    public ResponseEntity<MusicTestDTO> testMusic(@PathVariable int id, @RequestBody MusicTestDTO musicTestDTO) {
        musicTestDTO.setMusicId(id);
        MusicTestDTO testDTO = musicTestService.testMusic(musicTestDTO);
        return ResponseEntity.ok(testDTO);
    }

    @Operation(summary = "악보와 사용자별 도전 결과", description = "도전 데이터 생성")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<List<MusicTestDTO>> getTestsByUserAndMusic(@PathVariable int id, @PathVariable String userId) {
        List<MusicTestDTO> tests = musicTestService.getTestsByUserAndMusic(id, userId);
        return ResponseEntity.ok(tests);
    }
}
