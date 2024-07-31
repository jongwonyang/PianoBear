package kr.pianobear.application.repository;

import kr.pianobear.application.model.MusicPractice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MusicPracticeRepository extends JpaRepository<MusicPractice, Integer> {
    Optional<MusicPractice> findByMusicIdAndUserIdAndPracticeDate(int musicId, String userId, LocalDateTime practiceDate);
    List<MusicPractice> findByUserIdAndMusicId(String userId, int musicId);
    List<MusicPractice> findByMusicIdOrderByPracticeDateAsc(int musicId);
}
