package kr.pianobear.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pianobear.application.dto.TranscriberResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class TranscriberService {

    private final String FASTAPI_URL;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public TranscriberService(@Value("${application.fastapi-url}") String fastapiUrl,
                              RestTemplate restTemplate, ObjectMapper objectMapper) {
        FASTAPI_URL = fastapiUrl;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<TranscriberResponseDTO> uploadAudioToGpuServer(MultipartFile audioFile) {
        String url = FASTAPI_URL + "/upload-audio/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("file", new MultipartInputStreamFileResource(audioFile.getInputStream(), audioFile.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
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

    public ResponseEntity<InputStreamResource> downloadFromGpuServer(String path) {
        try {
            // FastAPI 서버의 파일 다운로드 URL 구성
            String encodedFilePath = URLEncoder.encode(path, StandardCharsets.UTF_8);
            String url = FASTAPI_URL + "/download/?file_path=" + encodedFilePath;

            // 다른 서버로 요청 보내기
            ResponseEntity<byte[]> serverResponse = restTemplate.getForEntity(new URI(url), byte[].class);

            if (serverResponse.getStatusCode() == HttpStatus.OK) {
                // 응답으로 받은 파일을 사용자에게 전달
                InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(serverResponse.getBody()));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentLength(serverResponse.getBody().length);
                headers.set("Content-Disposition", "attachment; filename=\"downloaded_file\"");

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
