package kr.pianobear.application.controller;

import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.service.MusicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/test")
public class ImgTestController {

    private final MusicService musicService;

    public ImgTestController(MusicService musicService) {
        this.musicService = musicService;
    }

    @PostMapping("/create-music")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<MusicDTO> createMusic(@RequestBody MusicDTO musicDTO) throws IOException {
        MusicDTO savedMusic = musicService.saveMusic(musicDTO);
        return ResponseEntity.ok(savedMusic);
    }

}
