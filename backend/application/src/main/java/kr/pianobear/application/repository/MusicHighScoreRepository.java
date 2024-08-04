package kr.pianobear.application.repository;

import kr.pianobear.application.model.MusicHighScore;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicHighScoreRepository extends JpaRepository<MusicHighScore, Integer> {
    Optional<MusicHighScore> findByMusicAndMember(Music music, Member member);
}
