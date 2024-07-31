package kr.pianobear.application.repository;

import io.lettuce.core.dynamic.annotation.Param;
import kr.pianobear.application.model.MusicPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MusicPracticeRepository extends JpaRepository<MusicPractice, Integer> {
    Optional<MusicPractice> findByMusicIdAndUserIdAndPracticeDate(int musicId, String userId, LocalDateTime practiceDate);
    List<MusicPractice> findByUserIdAndMusicId(String userId, int musicId);
    List<MusicPractice> findByMusicIdOrderByPracticeDateAsc(int musicId);

    @Query("SELECT mp FROM MusicPractice mp WHERE mp.userId = :userId AND mp.practiceDate >= :startDate AND mp.practiceDate < :endDate")
    List<MusicPractice> findAllByUserIdAndMonth(@Param("userId") String userId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
}
