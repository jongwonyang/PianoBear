package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.service.TranscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
//    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @Operation(summary = "입력 오디오 파일 업로드")
    public ResponseEntity<Void> uploadAudio(@RequestPart(name = "audioFile") MultipartFile audioFile) {
        System.out.println("audioFile = " + audioFile);
        transcriberService.uploadAudioToGpuServer(audioFile);
        return ResponseEntity.ok().build();
    }
}
