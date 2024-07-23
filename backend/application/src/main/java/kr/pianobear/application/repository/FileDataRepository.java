package kr.pianobear.application.repository;

import kr.pianobear.application.model.FileData;
import org.springframework.data.repository.CrudRepository;

public interface FileDataRepository extends CrudRepository<FileData, Long> {
}
