package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/music")
@Tag(name = "Music API", description = "Music management API")
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

    @Operation(summary = "악보 추가", description = "새로운 악보를 추가함")
    @PreAuthorize("hasRole('ROLE_MEMBER')") //로그인 없이 이용할 수 없다는 뜻의 annotation
    @PostMapping
    public ResponseEntity<MusicDTO> addMusic(@RequestBody MusicDTO musicDTO) {
        MusicDTO createdMusic = musicService.addMusic(musicDTO);
        return ResponseEntity.ok(createdMusic);
    }

    @Operation(summary = "모든 악보 불러오기", description = "사용자가 가지고 있는 모든 악보 불러온다")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping
    public ResponseEntity<List<MusicDTO>> getAllMusic() {
        List<MusicDTO> musicList = musicService.getAllMusic();
        return ResponseEntity.ok(musicList);
    }

    @Operation(summary = "특정 악보 불러오기", description = "ID로 악보 불러오기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<MusicDTO> getMusicById(@PathVariable int id) {
        Optional<MusicDTO> musicDTO = musicService.getMusicById(id);
        return musicDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "특정 악보 삭제", description = "악보 ID로 삭제")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusic(@PathVariable int id) {
        musicService.deleteMusic(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "찜하기", description = "악보의 찜 여부")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> favoriteMusic(@PathVariable int id, @RequestParam boolean favorite) {
        musicService.favoriteMusic(id, favorite);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "제목으로 검색", description = "악보를 제목으로 검색한다")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/search/title")
    public ResponseEntity<List<MusicDTO>> searchMusicByTitle(@RequestParam String title) {
        List<MusicDTO> musicList = musicService.searchMusicByTitle(title);
        return ResponseEntity.ok(musicList);
    }

    @Operation(summary = "작곡가로 검색", description = "악보를 작곡가로 검색한다")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/search/artist")
    public ResponseEntity<List<MusicDTO>> searchMusicByArtist(@RequestParam String artist) {
        List<MusicDTO> musicList = musicService.searchMusicByArtist(artist);
        return ResponseEntity.ok(musicList);
    }

    @Operation(summary = "페이징된 악보 목록", description = "페이징된 악보 목록 불러오기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/page")
    public ResponseEntity<Page<MusicSummaryDTO>> getPaginatedMusic(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String direction) {
        Page<MusicSummaryDTO> musicPage = musicService.getPaginatedMusic(page, size, sortBy, direction);
        return ResponseEntity.ok(musicPage);
    }

    @Operation(summary = "악보 연습", description = "악보 연습함으로서 추가한다")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/{id}/practice")
    public ResponseEntity<MusicPracticeDTO> practiceMusic(@PathVariable int id, @RequestParam String userId) {
        MusicPracticeDTO practiceDTO = musicService.practiceMusic(id, userId);
        return ResponseEntity.ok(practiceDTO);
    }

    @Operation(summary = "사용자와 악보에 따라 연습 기록 불러오기", description = "사용자가 어떤 곡을 몇 번 연습했는지 불러온다")
    @GetMapping("/{id}/practice/user/{userId}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<List<MusicPracticeDTO>> getPracticeDataByUserAndMusic(@PathVariable int id, @PathVariable String userId) {
        List<MusicPracticeDTO> practiceData = musicPracticeService.getPracticeDataByUserAndMusic(id, userId);
        return ResponseEntity.ok(practiceData);
    }

    @Operation(summary = "날짜로 분류된 연습 데이터 가져오기", description = "날짜별 연습 데이터")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}/practice/sorted")
    public ResponseEntity<List<MusicPracticeDTO>> getAllPracticeDataSortedByDate(@PathVariable int id) {
        List<MusicPracticeDTO> practiceData = musicPracticeService.getAllPracticeDataSortedByDate(id);
        return ResponseEntity.ok(practiceData);
    }

    @Operation(summary = "악보 도전", description = "도전 데이터 추가하기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/{id}/test")
    public ResponseEntity<MusicTestDTO> testMusic(@PathVariable int id, @RequestBody MusicTestDTO musicTestDTO) {
        musicTestDTO.setMusicId(id);
        MusicTestDTO testDTO = musicTestService.testMusic(musicTestDTO);
        return ResponseEntity.ok(testDTO);
    }

    @Operation(summary = "악보와 사용자별 도전 결과", description = "도전 데이터 생성")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}/test/user/{userId}")
    public ResponseEntity<List<MusicTestDTO>> getTestsByUserAndMusic(@PathVariable int id, @PathVariable String userId) {
        List<MusicTestDTO> tests = musicTestService.getTestsByUserAndMusic(id, userId);
        return ResponseEntity.ok(tests);
    }
}
