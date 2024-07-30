package kr.pianobear.application.repository;

import kr.pianobear.application.model.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MemberRepository extends CrudRepository<Member, String> {
    boolean existsByEmail(String email);
}
