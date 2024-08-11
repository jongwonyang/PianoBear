package kr.pianobear.application.service;

import kr.pianobear.application.model.FileData;
import kr.pianobear.application.repository.FileDataRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.parseMediaType;

@Service
public class FileDataService {

    private final String SAVE_PATH;
    private final FileDataRepository fileDataRepository;

    public FileDataService(
            @Value("${file.save-path}") String savePath,
            FileDataRepository fileDataRepository) {
        this.SAVE_PATH = savePath;
        this.fileDataRepository = fileDataRepository;
    }

    public FileData uploadFile(MultipartFile file) throws IOException {
        String savedName = UUID.randomUUID().toString();
        String path = SAVE_PATH + savedName;

        FileData fileData = new FileData();
        fileData.setOriginalName(file.getOriginalFilename());
        fileData.setSavedName(savedName);
        fileData.setPath(path);
        fileData.setType(file.getContentType());

        fileData = fileDataRepository.save(fileData);

        file.transferTo(new File(path));

        return fileData;
    }

    public FileData uploadFile(byte[] file, String originalName, String contentType) throws IOException {
        String savedName = UUID.randomUUID().toString();
        String path = SAVE_PATH + savedName;

        Path writePath = Paths.get(path);
        Files.write(writePath, file);

        FileData fileData = new FileData();
        fileData.setOriginalName(originalName);
        fileData.setSavedName(savedName);
        fileData.setPath(path);
        fileData.setType(contentType);
        fileData = fileDataRepository.save(fileData);

        return fileData;
    }

    public FileData uploadImage(MultipartFile imageFile, int width, int height) throws IOException {
        String savedName = UUID.randomUUID().toString();
        String path = SAVE_PATH + savedName;

        File tempFile = File.createTempFile("temp-", imageFile.getOriginalFilename());
        imageFile.transferTo(tempFile);

        Thumbnails.of(tempFile)
                .size(width, height)
                .toOutputStream(new FileOutputStream(path));

        tempFile.delete();

        FileData fileData = new FileData();

        fileData.setOriginalName(imageFile.getOriginalFilename());
        fileData.setSavedName(savedName);
        fileData.setPath(path);
        fileData.setType(imageFile.getContentType());

        fileData = fileDataRepository.save(fileData);

        return fileData;
    }

    public ResponseEntity<Resource> downloadFile(Long fileId) {
        FileData fileData = fileDataRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));

        Path filePath = Paths.get(fileData.getPath());
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found: " + fileData.getPath());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error while reading file: " + fileData.getPath(), e);
        }

        String contentType = fileData.getType();

        return ResponseEntity.ok()
                .contentType(parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.getOriginalName() + "\"")
                .body(resource);
    }

    public static Optional<String> getDownloadPath(FileData fileData) {
        if (fileData == null)
            return Optional.empty();

        return Optional.of("/api/v1/files/" + fileData.getId());
    }

    public FileData savePdfFile(MultipartFile file) throws IOException {
        Path directory = Paths.get(SAVE_PATH);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + fileExtension;
        String path = Paths.get(SAVE_PATH, savedName).toString();

        FileData fileData = new FileData();
        fileData.setOriginalName(originalFilename);
        fileData.setSavedName(savedName);
        fileData.setPath(path);
        fileData.setType(file.getContentType());

        fileData = fileDataRepository.save(fileData);

        file.transferTo(new File(path));

        return fileData;
    }

    public FileData saveFileToDirectory(MultipartFile file, String directoryPath) throws IOException {

        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + fileExtension;
        String path = Paths.get(directoryPath, savedName).toString();

        FileData fileData = new FileData();
        fileData.setOriginalName(originalFilename);
        fileData.setSavedName(savedName);
        fileData.setPath(path);
        fileData.setType(file.getContentType());

        fileData = fileDataRepository.save(fileData);

        file.transferTo(new File(path));

        return fileData;
    }

    public FileData saveFile(MultipartFile file) throws IOException {
        Path directory = Paths.get(SAVE_PATH);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + fileExtension;
        String path = Paths.get(SAVE_PATH, savedName).toString();

        FileData fileData = new FileData();
        fileData.setOriginalName(originalFilename);
        fileData.setSavedName(savedName);
        fileData.setPath(path);
        fileData.setType(file.getContentType());

        fileData = fileDataRepository.save(fileData);

        file.transferTo(new File(path));

        return fileData;
    }

}
