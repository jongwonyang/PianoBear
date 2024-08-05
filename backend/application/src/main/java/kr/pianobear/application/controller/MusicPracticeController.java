package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.MusicPracticeDTO;
import kr.pianobear.application.service.MusicPracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/music/practice")
@Tag(name = "Music Practice API", description = "Music practice management API")
public class MusicPracticeController {

    private final MusicPracticeService musicPracticeService;

    @Autowired
    public MusicPracticeController(MusicPracticeService musicPracticeService) {
        this.musicPracticeService = musicPracticeService;
    }

    @Operation(summary = "악보 연습", description = "악보 연습함으로서 추가한다")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/{id}")
    public ResponseEntity<MusicPracticeDTO> practiceMusic(@PathVariable int id, @RequestParam String userId) {
        MusicPracticeDTO practiceDTO = musicPracticeService.practiceMusic(id, userId);
        return ResponseEntity.ok(practiceDTO);
    }

    @Operation(summary = "사용자와 악보에 따라 연습 기록 불러오기", description = "사용자가 어떤 곡을 몇 번 연습했는지 불러온다")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<List<MusicPracticeDTO>> getPracticeDataByUserAndMusic(@PathVariable int id, @PathVariable String userId) {
        List<MusicPracticeDTO> practiceData = musicPracticeService.getPracticeDataByUserAndMusic(id, userId);
        return ResponseEntity.ok(practiceData);
    }

    @Operation(summary = "날짜로 분류된 연습 데이터 가져오기", description = "날짜별 연습 데이터")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}/sorted")
    public ResponseEntity<List<MusicPracticeDTO>> getAllPracticeDataSortedByDate(@PathVariable int id) {
        List<MusicPracticeDTO> practiceData = musicPracticeService.getAllPracticeDataSortedByDate(id);
        return ResponseEntity.ok(practiceData);
    }

    @Operation(summary = "월별 연습 기록 불러오기", description = "월별로 사용자의 연습 기록을 불러온다")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{userId}/monthly/{year}/{month}")
    public ResponseEntity<List<MusicPracticeDTO>> getMonthlyPracticeRecords(@PathVariable String userId, @PathVariable int year, @PathVariable int month) {
        List<MusicPracticeDTO> monthlyPracticeRecords = musicPracticeService.getMonthlyPracticeRecords(userId, year, month);
        return ResponseEntity.ok(monthlyPracticeRecords);
    }
}
