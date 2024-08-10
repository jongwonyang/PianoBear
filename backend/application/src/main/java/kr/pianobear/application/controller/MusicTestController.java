package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.MusicTestDTO;
import kr.pianobear.application.service.MusicTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/music/test")
@Tag(name = "Music Test API", description = "Music test management API")
public class MusicTestController {

    private final MusicTestService musicTestService;

    @Autowired
    public MusicTestController(MusicTestService musicTestService) {
        this.musicTestService = musicTestService;
    }

    @Operation(summary = "더미 테스트 데이터 추가", description = "더미 테스트 데이터를 추가합니다.")
    @PostMapping("/add-dummy")
    public ResponseEntity<MusicTestDTO> addDummyMusicTest(@RequestParam int musicId,
                                                          @RequestParam String userId,
                                                          @RequestParam int grade) {
        LocalDate testDate = LocalDate.now();
        MusicTestDTO createdTest = musicTestService.addDummyMusicTest(musicId, userId, grade, testDate);
        return ResponseEntity.ok(createdTest);
    }

    @Operation(summary = "악보 도전", description = "도전 데이터 추가하기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/{id}")
    public ResponseEntity<MusicTestDTO> testMusic(@PathVariable int id, @RequestBody MusicTestDTO musicTestDTO) {
        musicTestDTO.setMusicId(id);
        MusicTestDTO testDTO = musicTestService.testMusic(musicTestDTO);
        return ResponseEntity.ok(testDTO);
    }

    @Operation(summary = "악보와 사용자별 도전 결과", description = "도전 데이터 생성")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<List<MusicTestDTO>> getTestsByUserAndMusic(@PathVariable int id, @PathVariable String userId) {
        List<MusicTestDTO> tests = musicTestService.getTestsByUserAndMusic(id, userId);
        return ResponseEntity.ok(tests);
    }

    @Operation(summary = "도전 id에 대한 도전 결과", description = "도전 정보")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<MusicTestDTO> getResultById(@PathVariable int id){
        MusicTestDTO result = musicTestService.getResultById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "도전 결과에 대한 상장 만들기", description = "상장 만들기")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
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
