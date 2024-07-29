package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicTestDTO;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.model.MusicTest;
import kr.pianobear.application.repository.MusicRepository;
import kr.pianobear.application.repository.MusicTestRepository;
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

    @Autowired
    public MusicTestService(MusicTestRepository musicTestRepository, MusicRepository musicRepository) {
        this.musicTestRepository = musicTestRepository;
        this.musicRepository = musicRepository;
    }

    @Transactional
    public MusicTestDTO testMusic(MusicTestDTO musicTestDTO) {
        Optional<Music> optionalMusic = musicRepository.findById(musicTestDTO.getMusicId());
        if (!optionalMusic.isPresent()) {
            throw new RuntimeException("Music not found with id " + musicTestDTO.getMusicId());
        }

        Music music = optionalMusic.get();
        if (musicTestDTO.getGrade() > music.getHighestScore()) {
            music.setHighestScore(musicTestDTO.getGrade());
            musicRepository.save(music);
        }

        MusicTest musicTest = new MusicTest();
        musicTest.setGrade(musicTestDTO.getGrade());
        musicTest.setUserId(musicTestDTO.getUserId());
        musicTest.setMusicId(musicTestDTO.getMusicId());

        MusicTest savedTest = musicTestRepository.save(musicTest);
        return mapToDTO(savedTest);
    }

    public List<MusicTestDTO> getTestsByUserAndMusic(int musicId, String userId) {
        List<MusicTest> tests = musicTestRepository.findByUserIdAndMusicId(userId, musicId);
        return tests.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private MusicTestDTO mapToDTO(MusicTest musicTest) {
        MusicTestDTO dto = new MusicTestDTO();
        dto.setId(musicTest.getId());
        dto.setGrade(musicTest.getGrade());
        dto.setUserId(musicTest.getUserId());
        dto.setMusicId(musicTest.getMusicId());
        return dto;
    }
}
