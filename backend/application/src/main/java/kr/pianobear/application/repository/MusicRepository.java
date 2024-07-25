package kr.pianobear.application.repository;

import kr.pianobear.application.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Integer> {
    List<Music> findByUserIdOrderByTitleAsc(String userId);
    List<Music> findByUserIdOrderByPracticeCountDesc(String userId);
    List<Music> findByUserIdOrderByUploadDateDesc(String userId);
    List<Music> findByUserIdAndFavoriteTrueOrderByTitleAsc(String userId);
    List<Music> findByTitleContaining(String keyword);
    List<Music> findByArtistContaining(String keyword);  // Updated to search by composer
}
