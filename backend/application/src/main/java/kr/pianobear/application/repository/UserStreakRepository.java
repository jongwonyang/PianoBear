package kr.pianobear.application.repository;

import kr.pianobear.application.model.UserStreak;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStreakRepository extends CrudRepository<UserStreak, String> {
}
