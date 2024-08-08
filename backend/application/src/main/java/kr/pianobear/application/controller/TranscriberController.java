package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.pianobear.application.dto.TranscriberResponseDTO;
import kr.pianobear.application.service.TranscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
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
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String path) {
        return transcriberService.downloadFromGpuServer(path);
    }


}
