package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.model.MusicPractice;
import kr.pianobear.application.service.MusicPracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/music/practice")
@Tag(name = "MusicPractice", description = "연주 연습 API")
public class MusicPracticeController {
    @Autowired
    private MusicPracticeService musicPracticeService;

    @GetMapping("/byDay")
    @Operation(summary = "날짜별 연습")
    public List<MusicPractice> getPracticeByDay(@RequestParam String userId, @RequestParam int musicId, @RequestParam Date practiceDate) {
        return musicPracticeService.getPracticeByDay(userId, musicId, practiceDate);
    }

    @GetMapping("/history")
    @Operation(summary = "연습 기록")
    public List<MusicPractice> getPracticeHistory(@RequestParam String userId, @RequestParam int musicId) {
        return musicPracticeService.getPracticeHistory(userId, musicId);
    }

    @PostMapping
    @Operation(summary = "연습 기록 추가")
    public void addPractice(@RequestBody MusicPractice musicPractice) {
        musicPracticeService.addPractice(musicPractice);
    }
}
