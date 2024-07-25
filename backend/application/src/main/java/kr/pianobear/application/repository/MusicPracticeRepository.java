package kr.pianobear.application.repository;

import kr.pianobear.application.model.MusicPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface MusicPracticeRepository extends JpaRepository<MusicPractice, Integer> {
    List<MusicPractice> findByUserIdAndMusicIdAndPracticeDate(String userId, int musicId, Date practiceDate);
    List<MusicPractice> findByUserIdAndMusicIdOrderByPracticeDateAsc(String userId, int musicId);
}
