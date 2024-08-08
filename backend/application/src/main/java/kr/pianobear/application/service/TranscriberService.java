package kr.pianobear.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.dto.TranscriberResponseDTO;
import kr.pianobear.application.model.FileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class TranscriberService {

    private final String FASTAPI_URL;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final FileDataService fileDataService;
    private final MusicService musicService;

    @Autowired
    public TranscriberService(@Value("${application.fastapi-url}") String fastapiUrl,
                              RestTemplate restTemplate, ObjectMapper objectMapper, FileDataService fileDataService, MusicService musicService) {
        FASTAPI_URL = fastapiUrl;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.fileDataService = fileDataService;
        this.musicService = musicService;
    }

    public Optional<TranscriberResponseDTO> uploadAudioToGpuServer(MultipartFile audioFile) {
        String url = FASTAPI_URL + "/upload-audio/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            byte[] bytes = audioFile.getBytes();
            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            };
            body.add("file", byteArrayResource);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                TranscriberResponseDTO responseDTO = objectMapper.readValue(response.getBody(), TranscriberResponseDTO.class);
                return Optional.of(responseDTO);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public ResponseEntity<FileSystemResource> downloadFromGpuServer(String path) {
        try {
            // FastAPI 서버의 파일 다운로드 URL 구성
            String encodedFilePath = URLEncoder.encode(path, StandardCharsets.UTF_8);
            String url = FASTAPI_URL + "/download/?file_path=" + encodedFilePath;

            // 다른 서버로 요청 보내기
            ResponseEntity<byte[]> serverResponse = restTemplate.getForEntity(new URI(url), byte[].class);

            if (serverResponse.getStatusCode() == HttpStatus.OK) {
                // 응답으로 받은 파일을 사용자에게 전달
                byte[] fileBytes = serverResponse.getBody();
                Path tempFile = Files.createTempFile("downloaded-", ".tmp");
                Files.write(tempFile, fileBytes);

                FileSystemResource resource = new FileSystemResource(tempFile.toFile());

                // HTTP 응답 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentLength(fileBytes.length);
                headers.set("Content-Disposition", "attachment; filename=\"" + path.substring(path.lastIndexOf("/") + 1) + "\"");

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Optional<FileData> saveMxlFromGpuServer(String mxlPath) {
        try {
            // FastAPI 서버의 파일 다운로드 URL 구성
            String encodedFilePath = URLEncoder.encode(mxlPath, StandardCharsets.UTF_8);
            String url = FASTAPI_URL + "/download/?file_path=" + encodedFilePath;

            ResponseEntity<byte[]> serverResponse = restTemplate.getForEntity(new URI(url), byte[].class);
            if (serverResponse.getStatusCode() == HttpStatus.OK) {
                byte[] fileBytes = serverResponse.getBody();
                String originalName = mxlPath.substring(mxlPath.lastIndexOf("/") + 1);

                return Optional.of(fileDataService.uploadFile(fileBytes, originalName, "application/mxl"));
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<MusicDTO> addMxlToMe(String mxlPath) {
        try {
            Optional<FileData> fileData = saveMxlFromGpuServer(mxlPath);

            if (fileData.isEmpty())
                return Optional.empty();

            MusicDTO musicDTO = musicService.fileDataToMusicDTO(fileData.get());
            MusicDTO savedMusicDTO = musicService.saveMusic(musicDTO);
            return Optional.of(savedMusicDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() {
            try {
                return getInputStream().available();
            } catch (IOException e) {
                return -1;
            }
        }
    }
}
