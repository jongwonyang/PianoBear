package kr.pianobear.application.controller;

import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.dto.MusicPracticeDTO;
import kr.pianobear.application.dto.MusicSummaryDTO;
import kr.pianobear.application.dto.MusicTestDTO;
import kr.pianobear.application.service.MusicService;
import kr.pianobear.application.service.MusicPracticeService;
import kr.pianobear.application.service.MusicTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private final MusicService musicService;
    private final MusicPracticeService musicPracticeService;
    private final MusicTestService musicTestService;

    @Autowired
    public MusicController(MusicService musicService, MusicPracticeService musicPracticeService, MusicTestService musicTestService) {
        this.musicService = musicService;
        this.musicPracticeService = musicPracticeService;
        this.musicTestService = musicTestService;
    }

    @PostMapping
    public ResponseEntity<MusicDTO> addMusic(@RequestBody MusicDTO musicDTO) {
        MusicDTO createdMusic = musicService.addMusic(musicDTO);
        return ResponseEntity.ok(createdMusic);
    }

    @GetMapping
    public ResponseEntity<List<MusicDTO>> getAllMusic() {
        List<MusicDTO> musicList = musicService.getAllMusic();
        return ResponseEntity.ok(musicList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicDTO> getMusicById(@PathVariable int id) {
        Optional<MusicDTO> musicDTO = musicService.getMusicById(id);
        return musicDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusic(@PathVariable int id) {
        musicService.deleteMusic(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> favoriteMusic(@PathVariable int id, @RequestParam boolean favorite) {
        musicService.favoriteMusic(id, favorite);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<MusicDTO>> searchMusicByTitle(@RequestParam String title) {
        List<MusicDTO> musicList = musicService.searchMusicByTitle(title);
        return ResponseEntity.ok(musicList);
    }

    @GetMapping("/search/artist")
    public ResponseEntity<List<MusicDTO>> searchMusicByArtist(@RequestParam String artist) {
        List<MusicDTO> musicList = musicService.searchMusicByArtist(artist);
        return ResponseEntity.ok(musicList);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<MusicSummaryDTO>> getPaginatedMusic(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String direction) {
        Page<MusicSummaryDTO> musicPage = musicService.getPaginatedMusic(page, size, sortBy, direction);
        return ResponseEntity.ok(musicPage);
    }

    @PostMapping("/{id}/practice")
    public ResponseEntity<MusicPracticeDTO> practiceMusic(@PathVariable int id, @RequestParam String userId) {
        MusicPracticeDTO practiceDTO = musicService.practiceMusic(id, userId);
        return ResponseEntity.ok(practiceDTO);
    }

    @GetMapping("/{id}/practice/user/{userId}")
    public ResponseEntity<List<MusicPracticeDTO>> getPracticeDataByUserAndMusic(@PathVariable int id, @PathVariable String userId) {
        List<MusicPracticeDTO> practiceData = musicPracticeService.getPracticeDataByUserAndMusic(id, userId);
        return ResponseEntity.ok(practiceData);
    }

    @GetMapping("/{id}/practice/sorted")
    public ResponseEntity<List<MusicPracticeDTO>> getAllPracticeDataSortedByDate(@PathVariable int id) {
        List<MusicPracticeDTO> practiceData = musicPracticeService.getAllPracticeDataSortedByDate(id);
        return ResponseEntity.ok(practiceData);
    }

    @PostMapping("/{id}/test")
    public ResponseEntity<MusicTestDTO> testMusic(@PathVariable int id, @RequestBody MusicTestDTO musicTestDTO) {
        musicTestDTO.setMusicId(id);
        MusicTestDTO testDTO = musicTestService.testMusic(musicTestDTO);
        return ResponseEntity.ok(testDTO);
    }

    @GetMapping("/{id}/test/user/{userId}")
    public ResponseEntity<List<MusicTestDTO>> getTestsByUserAndMusic(@PathVariable int id, @PathVariable String userId) {
        List<MusicTestDTO> tests = musicTestService.getTestsByUserAndMusic(id, userId);
        return ResponseEntity.ok(tests);
    }
}
