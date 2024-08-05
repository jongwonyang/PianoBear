package kr.pianobear.application.repository;

import kr.pianobear.application.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MusicRepository extends JpaRepository<Music, Integer> {
    List<Music> findByUserIdIsNull();
    Optional<Music> findByTitle(String title);
    List<Music> findByUserId(String userId);
    List<Music> findByTitleContainingIgnoreCase(String title);
    List<Music> findByArtistContainingIgnoreCase(String artist);

    List<Music> findByUserIdOrderByUploadDateDesc(String userId);

    List<Music> findByUserIdOrderByUploadDateAsc(String userId);

    List<Music> findByUserIdOrderByTitleAsc(String userId);

    List<Music> findByUserIdOrderByTitleDesc(String userId);

    List<Music> findByUserIdOrderByFavoriteDesc(String userId);
}
