package kr.pianobear.application.service;

import kr.pianobear.application.model.Music;
import kr.pianobear.application.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicService {
    @Autowired
    private MusicRepository musicRepository;

    public List<Music> getAllMusic(String userId) {
        return musicRepository.findByUserIdOrderByTitleAsc(userId);
    }

    public void addMusic(Music music) {
        musicRepository.save(music);
    }

    public void deleteMusic(int id) {
        musicRepository.deleteById(id);
    }

    public void favoriteMusic(int id, boolean favorite) {
        Music music = musicRepository.findById(id).orElseThrow();
        music.setFavorite(favorite);
        musicRepository.save(music);
    }

    public Page<Music> getPagedMusicList(String userId, Pageable pageable) {
        return musicRepository.findAll(PageRequest.of(pageable.getPageNumber(), 15));
    }

    public List<Music> searchMusicByTitle(String keyword) {
        return musicRepository.findByTitleContaining(keyword);
    }

    public List<Music> searchMusicByComposer(String keyword) {
        return musicRepository.findByArtistContaining(keyword);  // Updated to search by composer
    }

    public List<Music> getSortedMusic(String userId, String sortType) {
        switch (sortType) {
            case "practiceCount":
                return musicRepository.findByUserIdOrderByPracticeCountDesc(userId);
            case "uploadDate":
                return musicRepository.findByUserIdOrderByUploadDateDesc(userId);
            case "favorite":
                return musicRepository.findByUserIdAndFavoriteTrueOrderByTitleAsc(userId);
            default:
                return musicRepository.findByUserIdOrderByTitleAsc(userId);
        }
    }
}
