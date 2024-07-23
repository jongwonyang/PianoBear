package kr.pianobear.application.repository;

import kr.pianobear.application.model.EmailAuth;
import org.springframework.data.repository.CrudRepository;

public interface EmailAuthRepository extends CrudRepository<EmailAuth, String> {
}
