package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/music")
@Tag(name = "Music API", description = "Music management API")
public class MusicController {

    private final MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @Operation(summary = "더미 악보 데이터 추가", description = "더미 악보 데이터를 추가합니다.")
    @PostMapping("/add-dummy")
    public ResponseEntity<MusicDTO> addDummyMusic(@RequestParam String title,
                                                  @RequestParam String artist,
                                                  @RequestParam String userId,
                                                  @RequestParam String fileRoute,
                                                  @RequestParam String musicImg,
                                                  @RequestParam boolean favorite,
                                                  @RequestParam String uploadDate) {
        LocalDate parsedUploadDate = LocalDate.parse(uploadDate);
        MusicDTO createdMusic = musicService.addDummyMusic(title, artist, userId, fileRoute, musicImg, favorite, parsedUploadDate);
        return ResponseEntity.ok(createdMusic);
    }

    @Operation(summary = "PDF 업로드", description = "PDF 파일을 업로드하고 악보를 추가합니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/upload")
    public ResponseEntity<MusicDTO> uploadPdf(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Received file: " + file.getOriginalFilename());

        MusicDTO createdMusic = musicService.uploadPdf(file);

        System.out.println("Created music: " + createdMusic);

        return ResponseEntity.ok(createdMusic);
    }

    @Operation(summary = "PDF를 MusicXML로 변환", description = "PDF 파일을 MusicXML 파일로 변환합니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/{id}/convert")
    public ResponseEntity<MusicDTO> convertPdfToMusicXml(@PathVariable int id,
                                                         @RequestParam("useSax") boolean useSax) throws IOException, InterruptedException {
        MusicDTO convertedMusic = musicService.convertPdfToMusicXml(id, useSax);
        return ResponseEntity.ok(convertedMusic);
    }

    @Operation(summary = "악보 저장", description = "변환된 악보를 저장합니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/save")
    public ResponseEntity<MusicDTO> saveMusic(@RequestParam("file") MultipartFile file,
                                              @RequestParam("title") String title,
                                              @RequestParam("artist") String artist,
                                              @RequestParam("userId") String userId) throws IOException {
        MusicDTO musicDTO = new MusicDTO();
        musicDTO.setTitle(title);
        musicDTO.setArtist(artist);
        musicDTO.setUserId(userId);
        musicDTO.setUploadDate(LocalDate.now());

        MusicDTO createdMusic = musicService.saveMusic(musicDTO, file);
        return ResponseEntity.ok(createdMusic);
    }

    @Operation(summary = "모든 악보 불러오기", description = "사용자가 가지고 있는 모든 악보를 불러옵니다.")
//    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping
    public ResponseEntity<List<MusicDTO>> getAllMusic() {
        List<MusicDTO> musicList = musicService.getAllMusic();
        return ResponseEntity.ok(musicList);
    }

    @Operation(summary = "특정 악보 불러오기", description = "ID로 악보를 불러옵니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<MusicDTO> getMusicById(@PathVariable int id) {
        Optional<MusicDTO> musicDTO = musicService.getMusicById(id);
        return musicDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "특정 악보 삭제", description = "악보 ID로 삭제합니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusic(@PathVariable int id) {
        musicService.deleteMusic(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "찜하기", description = "악보를 찜합니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> favoriteMusic(@PathVariable int id, @RequestParam boolean favorite) {
        musicService.favoriteMusic(id, favorite);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "제목으로 검색", description = "악보를 제목으로 검색합니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/search/title")
    public ResponseEntity<List<MusicDTO>> searchMusicByTitle(@RequestParam String title) {
        List<MusicDTO> musicList = musicService.searchMusicByTitle(title);
        return ResponseEntity.ok(musicList);
    }

    @Operation(summary = "작곡가로 검색", description = "악보를 작곡가로 검색합니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/search/artist")
    public ResponseEntity<List<MusicDTO>> searchMusicByArtist(@RequestParam String artist) {
        List<MusicDTO> musicList = musicService.searchMusicByArtist(artist);
        return ResponseEntity.ok(musicList);
    }

    @Operation(summary = "업로드 날짜 불러오기", description = "사용자가 업로드한 악보들의 날짜를 불러옵니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/upload-dates")
    public ResponseEntity<List<LocalDate>> getUploadDates(@RequestParam String userId) {
        List<LocalDate> uploadDates = musicService.getUploadDates(userId);
        return ResponseEntity.ok(uploadDates);
    }

    @Operation(summary = "찜 여부 불러오기", description = "악보의 찜 여부를 불러옵니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}/favorite")
    public ResponseEntity<Boolean> getFavoriteStatus(@PathVariable int id) {
        boolean isFavorite = musicService.getFavoriteStatus(id);
        return ResponseEntity.ok(isFavorite);
    }

    @Operation(summary = "사용자 악보 정렬", description = "사용자가 가지고 있는 악보를 정렬합니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/user/{userId}/sort")
    public ResponseEntity<List<MusicDTO>> getMusicByUserAndSort(@PathVariable String userId,
                                                                @RequestParam String sortBy,
                                                                @RequestParam String direction) {
        List<MusicDTO> sortedMusicList = musicService.getMusicByUserAndSort(userId, sortBy, direction);
        return ResponseEntity.ok(sortedMusicList);
    }

    @Operation(summary = "최다 연습 곡 3개", description = "사용자가 가장 많이 연습한 곡 3개를 반환합니다.")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/top3-practiced")
    public ResponseEntity<List<MusicDTO>> getTop3Practiced(@RequestParam String userId) {
        List<MusicDTO> top3PracticedMusic = musicService.getTop3Practiced(userId);
        return ResponseEntity.ok(top3PracticedMusic);
    }
}
