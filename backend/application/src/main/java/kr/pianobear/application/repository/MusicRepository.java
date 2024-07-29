package kr.pianobear.application.repository;

import kr.pianobear.application.model.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Integer> {
    List<Music> findByTitleContainingIgnoreCase(String title);
    List<Music> findByArtistContainingIgnoreCase(String artist);
    Page<Music> findAll(Pageable pageable);
}
