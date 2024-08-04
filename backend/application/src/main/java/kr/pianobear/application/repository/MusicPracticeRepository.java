package kr.pianobear.application.repository;

import kr.pianobear.application.model.Music;
import kr.pianobear.application.model.MusicPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MusicPracticeRepository extends JpaRepository<MusicPractice, Integer> {
    Optional<MusicPractice> findByMusicAndMemberIdAndPracticeDate(Music music, String memberId, LocalDateTime practiceDate);
    List<MusicPractice> findByMemberIdAndMusic(String memberId, Music music);
    List<MusicPractice> findByMusicOrderByPracticeDateAsc(Music music);
    List<MusicPractice> findAllByMemberIdAndPracticeDateBetween(String memberId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT mp.music FROM MusicPractice mp WHERE mp.member.id = :memberId GROUP BY mp.music ORDER BY SUM(mp.practiceCount) DESC")
    List<Music> findTop3ByUserIdOrderByPracticeCountDesc(String memberId);

    @Query("SELECT mp FROM MusicPractice mp WHERE mp.member.id = :userId AND mp.practiceDate >= :startDate AND mp.practiceDate < :endDate")
    List<MusicPractice> findAllByUserIdAndMonth(String userId, LocalDateTime startDate, LocalDateTime endDate);
}
