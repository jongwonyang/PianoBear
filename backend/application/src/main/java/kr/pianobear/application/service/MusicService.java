package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.dto.MusicSummaryDTO;
import kr.pianobear.application.dto.MusicPracticeDTO;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MusicService {

    private final MusicRepository musicRepository;
    private final MusicPracticeService musicPracticeService;

    @Autowired
    public MusicService(MusicRepository musicRepository, MusicPracticeService musicPracticeService) {
        this.musicRepository = musicRepository;
        this.musicPracticeService = musicPracticeService;
    }

    @Transactional
    public MusicDTO addMusic(MusicDTO musicDTO) {
        Music music = new Music();
        music.setTitle(musicDTO.getTitle());
        music.setOriginalFileRoute(musicDTO.getOriginalFileRoute());
        music.setChangedFileRoute(musicDTO.getChangedFileRoute());
        music.setPracticeCount(0);
        music.setRecentPractice(null);
        music.setUserId(musicDTO.getUserId());
        music.setMusicImg(musicDTO.getMusicImg());
        music.setFavorite(null);
        music.setUploadDate(LocalDateTime.now());
        music.setArtist(musicDTO.getArtist());
        music.setHighestScore(0);

        Music savedMusic = musicRepository.save(music);
        return mapToDTO(savedMusic);
    }

    public List<MusicDTO> getAllMusic() {
        List<Music> musicList = musicRepository.findAll();
        return musicList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<MusicDTO> getMusicById(int id) {
        Optional<Music> music = musicRepository.findById(id);
        return music.map(this::mapToDTO);
    }

    @Transactional
    public void deleteMusic(int id) {
        musicRepository.deleteById(id);
    }

    @Transactional
    public void favoriteMusic(int id, boolean favorite) {
        Optional<Music> music = musicRepository.findById(id);
        if (music.isPresent()) {
            Music m = music.get();
            m.setFavorite(favorite);
            musicRepository.save(m);
        }
    }

    public List<MusicDTO> searchMusicByTitle(String title) {
        List<Music> musicList = musicRepository.findByTitleContainingIgnoreCase(title);
        return musicList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MusicDTO> searchMusicByArtist(String artist) {
        List<Music> musicList = musicRepository.findByArtistContainingIgnoreCase(artist);
        return musicList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Page<MusicSummaryDTO> getPaginatedMusic(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Music> musicPage = musicRepository.findAll(pageable);
        return musicPage.map(this::mapToSummaryDTO);
    }

    public MusicPracticeDTO practiceMusic(int musicId, String userId) {
        return musicPracticeService.practiceMusic(musicId, userId);
    }

    private MusicDTO mapToDTO(Music music) {
        MusicDTO musicDTO = new MusicDTO();
        musicDTO.setId(music.getId());
        musicDTO.setTitle(music.getTitle());
        musicDTO.setOriginalFileRoute(music.getOriginalFileRoute());
        musicDTO.setChangedFileRoute(music.getChangedFileRoute());
        musicDTO.setPracticeCount(music.getPracticeCount());
        musicDTO.setRecentPractice(music.getRecentPractice() != null ? music.getRecentPractice().toString() : null);
        musicDTO.setUserId(music.getUserId());
        musicDTO.setMusicImg(music.getMusicImg());
        musicDTO.setFavorite(music.getFavorite());
        musicDTO.setUploadDate(music.getUploadDate().format(DateTimeFormatter.ISO_DATE_TIME));
        musicDTO.setArtist(music.getArtist());
        musicDTO.setHighestScore(music.getHighestScore());
        return musicDTO;
    }

    private MusicSummaryDTO mapToSummaryDTO(Music music) {
        MusicSummaryDTO summaryDTO = new MusicSummaryDTO();
        summaryDTO.setMusicImg(music.getMusicImg());
        summaryDTO.setTitle(music.getTitle());
        summaryDTO.setArtist(music.getArtist());
        summaryDTO.setFavorite(music.getFavorite());
        return summaryDTO;
    }

    public List<Music> getTop3Practiced(String userId) {
        return musicRepository.findTop3ByUserIdOrderByPracticeCountDesc(userId);
    }
}
