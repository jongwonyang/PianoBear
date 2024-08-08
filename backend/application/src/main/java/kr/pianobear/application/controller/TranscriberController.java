package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.dto.TranscriberResponseDTO;
import kr.pianobear.application.service.TranscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transcriber")
@Tag(name = "Transcriber", description = "AI 채보")
public class TranscriberController {

    private final TranscriberService transcriberService;

    @Autowired
    public TranscriberController(TranscriberService transcriberService) {
        this.transcriberService = transcriberService;
    }

    @PostMapping(value = "/upload-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @Operation(summary = "입력 오디오 파일 업로드")
    public ResponseEntity<TranscriberResponseDTO> uploadAudio(@RequestPart(name = "audioFile") MultipartFile audioFile) {
        Optional<TranscriberResponseDTO> response = transcriberService.uploadAudioToGpuServer(audioFile);
        if (response.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.status(HttpStatus.OK).body(response.get());
    }

    @GetMapping("/download")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @Operation(summary = "변환 결과 파일 다운로드")
    public ResponseEntity<FileSystemResource> downloadFile(@RequestParam String path) {
        // FastAPI 서버에서 파일 다운로드 및 사용자에게 전달
        return transcriberService.downloadFromGpuServer(path);
    }

    @GetMapping("/add-to-me")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @Operation(summary = "내 보관함에 악보 추가")
    public ResponseEntity<MusicDTO> addMxlToMe(@RequestParam String mxlPath) {
        Optional<MusicDTO> musicDTO = transcriberService.addMxlToMe(mxlPath);

        if (musicDTO.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return ResponseEntity.status(HttpStatus.OK).body(musicDTO.get());
    }
}
