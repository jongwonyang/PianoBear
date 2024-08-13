package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.MusicTestDTO;
import kr.pianobear.application.service.MusicTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/music/test")
@Tag(name = "Music Test API", description = "Music test management API")
public class MusicTestController {

    private final MusicTestService musicTestService;

    @Autowired
    public MusicTestController(MusicTestService musicTestService) {
        this.musicTestService = musicTestService;
    }

    @Operation(summary = "악보 도전", description = "도전 데이터 추가하기")
    @PostMapping(value = "/{sheetId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<MusicTestDTO> uploadAudio(@PathVariable int sheetId,
            @RequestPart(name = "audioFile") MultipartFile audioFile) {
        Optional<MusicTestDTO> response = musicTestService.testMusic(sheetId, audioFile);
        if (response.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.status(HttpStatus.OK).body(response.get());
    }

    @Operation(summary = "악보와 사용자별 도전 결과", description = "도전 데이터 생성")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{sheetId}/user/{userId}")
    public ResponseEntity<List<MusicTestDTO>> getTestsByUserAndMusic(@PathVariable int sheetId,
            @PathVariable String userId) {
        List<MusicTestDTO> tests = musicTestService.getTestsByUserAndMusic(sheetId, userId);
        return ResponseEntity.ok(tests);
    }

    @Operation(summary = "도전 id에 대한 도전 결과", description = "도전 정보")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<MusicTestDTO> getResultById(@PathVariable int id) {
        MusicTestDTO result = musicTestService.getResultById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "도전 결과에 대한 상장 만들기", description = "상장 만들기")
    @GetMapping("/{id}/award")
    public ResponseEntity<String> generateAward(@PathVariable("id") int id) {
        // 도전 ID를 이용해 관련 데이터를 조회
        MusicTestDTO musicTestDTO = musicTestService.getResultById(id);

        // Thymeleaf Context 생성
        Context context = new Context();
        context.setVariable("grade", musicTestDTO.getGrade());
        context.setVariable("userId", musicTestDTO.getUserId());
        context.setVariable("musicId", musicTestDTO.getMusicId());
        context.setVariable("testDate", musicTestDTO.getTestDate());

        // TemplateEngine 설정
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);

        templateEngine.setTemplateResolver(templateResolver);

        // 상장 템플릿 처리
        String processedHtml = templateEngine.process("challenge-award", context);

        // HTML을 반환
        return ResponseEntity.ok().body(processedHtml);
    }
}
