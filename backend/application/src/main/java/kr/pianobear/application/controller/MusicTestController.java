package kr.pianobear.application.controller;

import kr.pianobear.application.model.MusicTest;
import kr.pianobear.application.service.MusicTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/v1/music") //다시 api 어떤 사용자 안에 있는 리소스 계층 구조
@Tag(name = "MusicTest", description = "연주 도전 API")
public class MusicTestController {
    @Autowired
    private MusicTestService musicTestService;

    @GetMapping("/test")
    @Operation(summary = "도전 기록")
    public List<MusicTest> getTestsByUser(@RequestParam String userId) {
        return musicTestService.getTestsByUser(userId);
    }

    @PostMapping
    @Operation(summary = "악보 도전")
    public void addTest(@RequestBody MusicTest musicTest) {
        musicTestService.addTest(musicTest);
    }
}
