package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicPracticeDTO;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.model.MusicPractice;
import kr.pianobear.application.repository.MusicPracticeRepository;
import kr.pianobear.application.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MusicPracticeService {

    private final MusicPracticeRepository musicPracticeRepository;
    private final MusicRepository musicRepository;

    @Autowired
    public MusicPracticeService(MusicPracticeRepository musicPracticeRepository, MusicRepository musicRepository) {
        this.musicPracticeRepository = musicPracticeRepository;
        this.musicRepository = musicRepository;
    }

    @Transactional
    public MusicPracticeDTO practiceMusic(int musicId, String userId) {
        Optional<Music> optionalMusic = musicRepository.findById(musicId);
        if (!optionalMusic.isPresent()) {
            throw new RuntimeException("Music not found with id " + musicId);
        }

        Music music = optionalMusic.get();
        music.setPracticeCount(music.getPracticeCount() + 1);
        music.setRecentPractice(LocalDate.now().toString());
        musicRepository.save(music);

        LocalDateTime today = LocalDate.now().atStartOfDay();

        Optional<MusicPractice> optionalMusicPractice = musicPracticeRepository.findByMusicIdAndUserIdAndPracticeDate(musicId, userId, today);
        MusicPractice musicPractice;
        if (optionalMusicPractice.isPresent()) {
            musicPractice = optionalMusicPractice.get();
            musicPractice.setPracticeCount(musicPractice.getPracticeCount() + 1);
        } else {
            musicPractice = new MusicPractice();
            musicPractice.setMusicId(musicId);
            musicPractice.setUserId(userId);
            musicPractice.setPracticeDate(today);
            musicPractice.setPracticeCount(1);
        }

        MusicPractice savedPractice = musicPracticeRepository.save(musicPractice);
        return mapToDTO(savedPractice);
    }

    public List<MusicPracticeDTO> getPracticeDataByUserAndMusic(int musicId, String userId) {
        List<MusicPractice> practiceData = musicPracticeRepository.findByUserIdAndMusicId(userId, musicId);
        return practiceData.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MusicPracticeDTO> getAllPracticeDataSortedByDate(int musicId) {
        List<MusicPractice> practiceData = musicPracticeRepository.findByMusicIdOrderByPracticeDateAsc(musicId);
        return practiceData.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private MusicPracticeDTO mapToDTO(MusicPractice musicPractice) {
        MusicPracticeDTO dto = new MusicPracticeDTO();
        dto.setId(musicPractice.getId());
        dto.setPracticeDate(musicPractice.getPracticeDate().toString());
        dto.setPracticeCount(musicPractice.getPracticeCount());
        dto.setMusicId(musicPractice.getMusicId());
        dto.setUserId(musicPractice.getUserId());
        return dto;
    }
}
