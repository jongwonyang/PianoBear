package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicTestDTO;
import kr.pianobear.application.dto.TranscriberResponseDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.model.MusicHighScore;
import kr.pianobear.application.model.MusicTest;
import kr.pianobear.application.repository.MusicRepository;
import kr.pianobear.application.repository.MusicTestRepository;
import kr.pianobear.application.util.SecurityUtil;
import kr.pianobear.application.repository.MusicHighScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MusicTestService {

    private final MusicTestRepository musicTestRepository;
    private final MusicRepository musicRepository;
    private final MusicHighScoreRepository musicHighScoreRepository;
    private final MemberRepository memberRepository;

    private final String COMPARE_API_URL;
    private final RestTemplate restTemplate;

    @Autowired
    public MusicTestService(@Value("${application.compare-server-url}") String compareApiUrl,
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            MusicTestRepository musicTestRepository, MusicRepository musicRepository,
            MusicHighScoreRepository musicHighScoreRepository, MemberRepository memberRepository) {
        this.musicTestRepository = musicTestRepository;
        this.musicRepository = musicRepository;
        this.musicHighScoreRepository = musicHighScoreRepository;
        this.memberRepository = memberRepository;

        COMPARE_API_URL = compareApiUrl;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public MusicTestDTO addDummyMusicTest(int musicId, String userId, int grade, LocalDate testDate) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("Music not found with id " + musicId));
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        MusicTest musicTest = new MusicTest();
        musicTest.setMusic(music);
        musicTest.setMember(member);
        musicTest.setGrade(grade);
        musicTest.setTestDate(testDate);

        MusicTest savedTest = musicTestRepository.save(musicTest);
        return mapToDTO(savedTest);
    }

    @Transactional
    public Optional<MusicTestDTO> testMusic(int sheetId, MultipartFile audioFile) {

        Optional<Music> musicOpt = musicRepository.findById(sheetId);

        if (musicOpt.isEmpty()) {
            throw new RuntimeException("Music not found with id " + sheetId);
        }

        Music music = musicOpt.get();
        String currentUserId = SecurityUtil.getCurrentUserId();

        Optional<Member> memberOpt = memberRepository.findById(currentUserId);
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("User not found with id " + sheetId);
        }

        Member member = memberOpt.get();

        String url = COMPARE_API_URL + "/compare";

        // 1. FileSystemResource로 musicxml 파일 읽기
        FileSystemResource musicXmlResource = new FileSystemResource(new File(music.getModifiedMusicXmlRoute()));

        // 2. Multipart WAV 파일을 MultiValueMap에 추가
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("musicxml_file", musicXmlResource);
            body.add("wav_file", audioFile.getResource());
        } catch (Exception e) {
            throw new RuntimeException("file open failed." + e.getStackTrace());
        }

        // 3. HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 4. HttpEntity 객체 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            float score = Float.parseFloat(response.getBody());

            MusicTestDTO newDTO = new MusicTestDTO();
            newDTO.setUserId(currentUserId);
            newDTO.setMusicId(sheetId);
            if (score < 50) {
                score = 0;
            } else if (score >= 60) {
                score = 10;
            } else {
                score -= 50;
            }
            newDTO.setGrade((int) (50 + score * 5));
            newDTO.setTestDate(LocalDate.now());

            Optional<MusicHighScore> optionalHighScore = musicHighScoreRepository.findByMusicAndMember(music, member);
            MusicHighScore musicHighScore;
            if (optionalHighScore.isPresent()) {
                musicHighScore = optionalHighScore.get();
                if (newDTO.getGrade() > musicHighScore.getScore()) {
                    musicHighScore.setScore(newDTO.getGrade());
                    musicHighScoreRepository.save(musicHighScore);
                }
            } else {
                musicHighScore = new MusicHighScore();
                musicHighScore.setMusic(music);
                musicHighScore.setMember(member);
                musicHighScore.setScore(newDTO.getGrade());
                musicHighScoreRepository.save(musicHighScore);
            }

            MusicTest musicTest = new MusicTest();
            musicTest.setGrade(newDTO.getGrade());
            musicTest.setMember(member);
            musicTest.setMusic(music);
            musicTest.setTestDate(newDTO.getTestDate());

            MusicTest savedTest = musicTestRepository.save(musicTest);
            return Optional.of(mapToDTO(savedTest));
        } else {
            return Optional.of(null);
        }
    }

    public List<MusicTestDTO> getTestsByUserAndMusic(int musicId, String userId) {
        List<MusicTest> tests = musicTestRepository.findByMemberIdAndMusicId(userId, musicId);
        return tests.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public MusicTestDTO getResultById(int id) {
        MusicTest result = musicTestRepository.findResultById(id);
        return mapToDTO(result);
    }

    private MusicTestDTO mapToDTO(MusicTest musicTest) {
        MusicTestDTO dto = new MusicTestDTO();
        dto.setId(musicTest.getId());
        dto.setGrade(musicTest.getGrade());
        dto.setTestDate(musicTest.getTestDate());
        dto.setUserId(musicTest.getMember().getId());
        dto.setMusicId(musicTest.getMusic().getId());
        return dto;
    }
}
