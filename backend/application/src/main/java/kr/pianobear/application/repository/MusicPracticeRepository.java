package kr.pianobear.application.repository;

import io.lettuce.core.dynamic.annotation.Param;
import kr.pianobear.application.model.MusicPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MusicPracticeRepository extends JpaRepository<MusicPractice, Integer> {
    Optional<MusicPractice> findByMusicIdAndMemberIdAndPracticeDate(int musicId, String memberId, LocalDateTime practiceDate);
    List<MusicPractice> findByMemberIdAndMusicId(String memberId, int musicId);
    List<MusicPractice> findByMusicIdOrderByPracticeDateAsc(int musicId);
    List<MusicPractice> findAllByMemberIdAndPracticeDateBetween(String memberId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT mp.music FROM MusicPractice mp WHERE mp.member.id = :memberId GROUP BY mp.music ORDER BY SUM(mp.practiceCount) DESC")
    List<MusicPractice> findTop3ByUserIdOrderByPracticeCountDesc(String memberId);

    @Query("SELECT mp FROM MusicPractice mp WHERE mp.member.id = :memberId AND mp.practiceDate >= :startDate AND mp.practiceDate < :endDate")
    List<MusicPractice> findAllByUserIdAndMonth(@Param("memberId") String memberId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
}
