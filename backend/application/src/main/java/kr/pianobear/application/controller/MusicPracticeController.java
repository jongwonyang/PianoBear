package kr.pianobear.application.controller;

import kr.pianobear.application.model.MusicPractice;
import kr.pianobear.application.service.MusicPracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/music/practice")
public class MusicPracticeController {
    @Autowired
    private MusicPracticeService musicPracticeService;

    @GetMapping("/byDay")
    public List<MusicPractice> getPracticeByDay(@RequestParam String userId, @RequestParam int musicId, @RequestParam Date practiceDate) {
        return musicPracticeService.getPracticeByDay(userId, musicId, practiceDate);
    }

    @GetMapping("/history")
    public List<MusicPractice> getPracticeHistory(@RequestParam String userId, @RequestParam int musicId) {
        return musicPracticeService.getPracticeHistory(userId, musicId);
    }

    @PostMapping("/add")
    public void addPractice(@RequestBody MusicPractice musicPractice) {
        musicPracticeService.addPractice(musicPractice);
    }
}
