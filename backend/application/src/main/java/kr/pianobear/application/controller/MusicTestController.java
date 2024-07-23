package kr.pianobear.application.controller;

import kr.pianobear.application.model.MusicTest;
import kr.pianobear.application.service.MusicTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/music/test") //다시 api 어떤 사용자 안에 있는 리소스 계층 구조
public class MusicTestController {
    @Autowired
    private MusicTestService musicTestService;

    @GetMapping("/byUser")
    public List<MusicTest> getTestsByUser(@RequestParam String userId) {
        return musicTestService.getTestsByUser(userId);
    }

    @PostMapping("/add")
    public void addTest(@RequestBody MusicTest musicTest) {
        musicTestService.addTest(musicTest);
    }
}
