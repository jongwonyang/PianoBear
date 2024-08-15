package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.service.MusicService;
import kr.pianobear.application.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/music")
@Tag(name = "Music API", description = "Music management API")
public class MusicController {

    private final MusicService musicService;
    private MusicDTO lastConvertedMusic;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @Operation(summary = "PDF 변환", description = "PDF 파일을 업로드하고 변환을 요청합니다.")
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MusicDTO> processPdfUpload(@RequestPart(name = "file") MultipartFile file) throws IOException, InterruptedException {
        MusicDTO createdMusic = musicService.processPdfUpload(file);
        this.lastConvertedMusic = createdMusic;
        return ResponseEntity.ok(createdMusic);
    }

    @Operation(summary = "Music 수정", description = "최근 변환된 Music의 제목을 수정합니다.")
    @PostMapping("/{id}/edit")
    public ResponseEntity<MusicDTO> saveMusic(@PathVariable int id, @RequestParam String title) throws IOException {
        MusicDTO savedMusic = musicService.editMusic(id, title);
        return ResponseEntity.ok(savedMusic);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveMusic(@RequestBody MusicDTO musicDTO) {
        try {
            MusicDTO savedMusic = musicService.saveMusic(musicDTO);
            return ResponseEntity.ok(savedMusic);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 검증 실패 시 400 Bad Request와 함께 메시지 반환
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File processing failed.");
        }
    }

    @Operation(summary = "모든 악보 불러오기", description = "사용자가 가지고 있는 모든 악보를 불러옵니다.")
    @GetMapping
    public ResponseEntity<List<MusicDTO>> getAllMusic() {
        List<MusicDTO> musicList = musicService.getAllMusic();
        return ResponseEntity.ok(musicList);
    }

    @Operation(summary = "특정 악보 불러오기", description = "ID로 악보를 불러옵니다.")
    @GetMapping("/{id}")
    public ResponseEntity<MusicDTO> getMusicById(@PathVariable int id) {
        Optional<MusicDTO> musicDTO = musicService.getMusicById(id);
        return musicDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "특정 악보 삭제", description = "악보 ID로 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusic(@PathVariable int id) {
        musicService.deleteMusic(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "찜하기/찜 해제", description = "악보를 찜하거나 찜을 해제합니다.")
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> favoriteMusic(@PathVariable int id, @RequestParam boolean favorite) {
        musicService.favoriteMusic(id, favorite);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "제목으로 검색", description = "악보를 제목으로 검색합니다.")
    @GetMapping("/search/title")
    public ResponseEntity<List<MusicDTO>> searchMusicByTitle(@RequestParam String title) {
        List<MusicDTO> musicList = musicService.searchMusicByTitle(title);
        return ResponseEntity.ok(musicList);
    }

    @Operation(summary = "작곡가로 검색", description = "악보를 작곡가로 검색합니다.")
    @GetMapping("/search/artist")
    public ResponseEntity<List<MusicDTO>> searchMusicByArtist(@RequestParam String artist) {
        List<MusicDTO> musicList = musicService.searchMusicByArtist(artist);
        return ResponseEntity.ok(musicList);
    }

    @Operation(summary = "업로드 날짜 불러오기", description = "사용자가 업로드한 악보들의 날짜를 불러옵니다.")
    @GetMapping("/upload-dates")
    public ResponseEntity<List<LocalDate>> getUploadDates(@RequestParam String userId) {
        List<LocalDate> uploadDates = musicService.getUploadDates(userId);
        return ResponseEntity.ok(uploadDates);
    }

    @Operation(summary = "찜 여부 불러오기", description = "악보의 찜 여부를 불러옵니다.")
    @GetMapping("/{id}/favorite")
    public ResponseEntity<Boolean> getFavoriteStatus(@PathVariable int id) {
        boolean isFavorite = musicService.getFavoriteStatus(id);
        return ResponseEntity.ok(isFavorite);
    }

    @Operation(summary = "사용자 악보 정렬", description = "사용자가 가지고 있는 악보를 정렬합니다.")
    @GetMapping("/user/{userId}/sort")
    public ResponseEntity<List<MusicDTO>> getMusicByUserAndSort(@PathVariable String userId,
                                                                @RequestParam String sortBy,
                                                                @RequestParam String direction) {
        List<MusicDTO> sortedMusicList = musicService.getMusicByUserAndSort(userId, sortBy, direction);
        return ResponseEntity.ok(sortedMusicList);
    }

    @Operation(summary = "최다 연습 곡 3개", description = "사용자가 가장 많이 연습한 곡 3개를 반환합니다.")
    @GetMapping("/top3-practiced")
    public ResponseEntity<List<MusicDTO>> getTop3Practiced(@RequestParam String userId) {
        List<MusicDTO> top3PracticedMusic = musicService.getTop3Practiced(userId);
        return ResponseEntity.ok(top3PracticedMusic);
    }

    @Operation(summary = "악보 이미지 불러오기", description = "악보의 이미지 경로를 불러옵니다.")
    @GetMapping("/{id}/music-img")
    public ResponseEntity<String> getMusicImgPath(@PathVariable int id) {
        String musicImgPath = musicService.getMusicImgPath(id);
        return ResponseEntity.ok(musicImgPath);
    }

    @GetMapping("/api/v1/music/{id}/download-music-xml")
    public ResponseEntity<Resource> downloadMusicXml(@PathVariable int id) {
        String musicXmlRoute = musicService.getMusicXmlRoute(id);
        return getFileResponse(musicXmlRoute);
    }

    @GetMapping("/api/v1/music/{id}/download-modified-music-xml")
    public ResponseEntity<Resource> downloadModifiedMusicXml(@PathVariable int id) {
        String modifiedMusicXmlRoute = musicService.getModifiedMusicXmlRoute(id);
        return getFileResponse(modifiedMusicXmlRoute);
    }

    @Operation(summary = "음악 이미지 보기", description = "ID로 음악 이미지를 브라우저에서 직접 보여줍니다.")
    @GetMapping("/{id}/view-music-img")
    public ResponseEntity<Resource> viewMusicImage(@PathVariable int id) {
        String musicImgPath = musicService.getMusicImgPath(id);
        return getInlineFileResponse(musicImgPath);
    }

    @Operation(summary = "음악 이미지 다운로드", description = "ID로 음악 이미지를 다운로드합니다.")
    @GetMapping("/{id}/download-music-img")
    public ResponseEntity<Resource> downloadMusicImage(@PathVariable int id) {
        String musicImgPath = musicService.getMusicImgPath(id);
        return getFileResponse(musicImgPath);
    }

    @Operation(summary = "악보 이미지 생성", description = "악보의 이미지를 생성하고 저장합니다.")
    @PostMapping("/{id}/generate-image")
    public ResponseEntity<MusicDTO> generateMusicImage(@PathVariable int id) throws IOException {
        MusicDTO musicWithImage = musicService.generateAndSaveImage(id);
        return ResponseEntity.ok(musicWithImage);
    }

    private ResponseEntity<Resource> getFileResponse(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Resource resource = new FileSystemResource(path.toFile());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Could not read file: " + filePath);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM) // MIME 타입 설정
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    private ResponseEntity<Resource> getInlineFileResponse(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Resource resource = new FileSystemResource(path.toFile());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Could not read file: " + filePath);
            }

            String mimeType = Files.probeContentType(path);
            if (mimeType == null) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Admin 사용자가 올린 악보 불러오기", description = "Admin 사용자가 올린 악보를 불러옵니다.")
    @GetMapping("/admin")
    public ResponseEntity<List<MusicDTO>> getAdminMusic() {
        List<MusicDTO> adminMusicList = musicService.getAdminMusic();
        return ResponseEntity.ok(adminMusicList);
    }

    @Operation(summary = "본인이 올린 악보 불러오기", description = "로그인한 사용자가 올린 악보를 불러옵니다.")
    @GetMapping("/user")
    public ResponseEntity<List<MusicDTO>> getUserMusic() {
        List<MusicDTO> userMusicList = musicService.getUserMusic();
        return ResponseEntity.ok(userMusicList);
    }
}