package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicTestDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.model.MusicHighScore;
import kr.pianobear.application.model.MusicTest;
import kr.pianobear.application.repository.MusicRepository;
import kr.pianobear.application.repository.MusicTestRepository;
import kr.pianobear.application.repository.MusicHighScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public MusicTestService(MusicTestRepository musicTestRepository, MusicRepository musicRepository, MusicHighScoreRepository musicHighScoreRepository, MemberRepository memberRepository) {
        this.musicTestRepository = musicTestRepository;
        this.musicRepository = musicRepository;
        this.musicHighScoreRepository = musicHighScoreRepository;
        this.memberRepository = memberRepository;
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
    public MusicTestDTO testMusic(MusicTestDTO musicTestDTO) {
        Optional<Music> optionalMusic = musicRepository.findById(musicTestDTO.getMusicId());
        if (optionalMusic.isEmpty()) {
            throw new RuntimeException("Music not found with id " + musicTestDTO.getMusicId());
        }

        Music music = optionalMusic.get();
        Member member = new Member(musicTestDTO.getUserId());

        Optional<MusicHighScore> optionalHighScore = musicHighScoreRepository.findByMusicAndMember(music, member);
        MusicHighScore musicHighScore;
        if (optionalHighScore.isPresent()) {
            musicHighScore = optionalHighScore.get();
            if (musicTestDTO.getGrade() > musicHighScore.getScore()) {
                musicHighScore.setScore(musicTestDTO.getGrade());
                musicHighScoreRepository.save(musicHighScore);
            }
        } else {
            musicHighScore = new MusicHighScore();
            musicHighScore.setMusic(music);
            musicHighScore.setMember(member);
            musicHighScore.setScore(musicTestDTO.getGrade());
            musicHighScoreRepository.save(musicHighScore);
        }

        MusicTest musicTest = new MusicTest();
        musicTest.setGrade(musicTestDTO.getGrade());
        musicTest.setMember(member);
        musicTest.setMusic(music);

        MusicTest savedTest = musicTestRepository.save(musicTest);
        return mapToDTO(savedTest);
    }

    public List<MusicTestDTO> getTestsByUserAndMusic(int musicId, String userId) {
        List<MusicTest> tests = musicTestRepository.findByMemberIdAndMusicId(userId, musicId);
        return tests.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MusicTestDTO> getResultById(int id){
        List<MusicTest> result = musicTestRepository.findResultById(id);
        return result.stream().map(this::mapToDTO).collect(Collectors.toList());
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
