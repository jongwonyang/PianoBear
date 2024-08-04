package kr.pianobear.application.repository;

import kr.pianobear.application.model.MusicTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicTestRepository extends JpaRepository<MusicTest, Integer> {
    List<MusicTest> findByMemberIdAndMusicId(String userId, int musicId);
}
