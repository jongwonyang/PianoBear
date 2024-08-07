package kr.pianobear.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TranscriberService {

    private final String FASTAPI_URL;
    private final RestTemplate restTemplate;

    @Autowired
    public TranscriberService(@Value("${application.fastapi-url}") String fastapiUrl,
                              RestTemplate restTemplate) {
        FASTAPI_URL = fastapiUrl;
        this.restTemplate = restTemplate;
    }

    public void uploadAudioToGpuServer(MultipartFile audioFile) {
        String url = FASTAPI_URL + "/upload-audio/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("file", new MultipartInputStreamFileResource(audioFile.getInputStream(), audioFile.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("File uploaded successfully: " + response.getBody());
        } else {
            System.out.println("File upload failed: " + response.getStatusCode());
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
