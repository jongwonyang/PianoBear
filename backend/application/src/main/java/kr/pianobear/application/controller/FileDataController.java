package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.service.FileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File", description = "파일 API")
public class FileDataController {

    private final FileDataService fileDataService;

    @Autowired
    public FileDataController(FileDataService fileDataService) {
        this.fileDataService = fileDataService;
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "파일 다운로드")
    public ResponseEntity<Resource> serveFile(@PathVariable Long fileId) {
        return fileDataService.downloadFile(fileId);
    }
}
