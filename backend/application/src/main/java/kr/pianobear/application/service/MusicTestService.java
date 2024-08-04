package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicTestDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.model.MusicHighScore;
import kr.pianobear.application.model.MusicTest;
import kr.pianobear.application.repository.MusicRepository;
import kr.pianobear.application.repository.MusicTestRepository;
import kr.pianobear.application.repository.MusicHighScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MusicTestService {

    private final MusicTestRepository musicTestRepository;
    private final MusicRepository musicRepository;
    private final MusicHighScoreRepository musicHighScoreRepository;

    @Autowired
    public MusicTestService(MusicTestRepository musicTestRepository, MusicRepository musicRepository, MusicHighScoreRepository musicHighScoreRepository) {
        this.musicTestRepository = musicTestRepository;
        this.musicRepository = musicRepository;
        this.musicHighScoreRepository = musicHighScoreRepository;
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

    private MusicTestDTO mapToDTO(MusicTest musicTest) {
        MusicTestDTO dto = new MusicTestDTO();
        dto.setId(musicTest.getId());
        dto.setGrade(musicTest.getGrade());
        dto.setUserId(musicTest.getMember().getId());
        dto.setMusicId(musicTest.getMusic().getId());
        return dto;
    }
}
