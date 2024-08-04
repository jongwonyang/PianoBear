package kr.pianobear.application.repository;

import kr.pianobear.application.model.FileData;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FileDataRepository extends CrudRepository<FileData, Long> {
    boolean existsBySavedName(String savedName);
    Optional<FileData> findBySavedName(String savedName);
}
