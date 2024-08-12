package kr.pianobear.application.service;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OpenAiService {

    // OpenAI 이미지 생성 API 엔드포인트 URL
    private static final String OPENAI_URL = "https://api.openai.com/v1/images/generations";

    // OpenAI API 키를 application.properties에서 주입받음
    @Value("${openai.api.key}")
    private String openaiApiKey;

    // 생성된 이미지를 저장할 경로를 application.properties에서 주입받음
    @Value("${file.save-path}")
    private String savePath;

    // OkHttpClient 인스턴스 생성 (향상된 타임아웃 설정)
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    /**
     * 주어진 제목(title)을 기반으로 OpenAI의 API를 호출해 이미지를 생성하고 저장하는 메소드.
     * 생성된 이미지의 저장 경로를 반환한다.
     *
     * @param title 이미지 생성에 사용할 제목
     * @return 저장된 이미지 파일의 경로
     * @throws IOException 이미지 생성 또는 저장 중 오류 발생 시 예외를 던짐
     */
    public String generateImage(String title) throws IOException {
        // 요청에 사용할 JSON 객체 생성
        JSONObject json = new JSONObject();
        json.put("prompt", "create an album art for a song titled \"" + title + "\""); // 프롬프트에 제목과 함께 'album cover'를 추가
        json.put("n", 1); // 생성할 이미지 개수
        json.put("size", "1024x1024"); // 이미지 크기 설정
        json.put("model", "dall-e-3"); // 사용할 최신 모델 이름

        // JSON 데이터를 요청 본문으로 설정
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

        // 요청 생성
        Request request = new Request.Builder()
                .url(OPENAI_URL) // API 엔드포인트 설정
                .post(body) // POST 요청으로 설정
                .addHeader("Authorization", "Bearer " + openaiApiKey) // API 키를 헤더에 추가
                .build();

        // Retry logic: Attempt the request up to 3 times if it fails
        int retryCount = 0;
        while (retryCount < 5) {
            Response response = client.newCall(request).execute();
            try {
                if (response.isSuccessful()) {
                    // 응답이 성공적일 경우 응답 본문에서 이미지 URL을 추출
                    JSONObject responseJson = new JSONObject(response.body().string());
                    String imageUrl = responseJson.getJSONArray("data").getJSONObject(0).getString("url");

                    // 추출한 이미지 URL을 통해 이미지를 다운로드하고 저장 경로를 반환
                    return downloadAndSaveImage(imageUrl, title);
                } else {
                    retryCount++;
                    if (retryCount == 3) {
                        throw new IOException("Failed to generate image after 5 attempts: " + response.body().string());
                    }
                }
            } finally {
                response.close();
            }
        }
        throw new IOException("Failed to generate image after 3 attempts.");
    }

    /**
     * 주어진 이미지 URL에서 이미지를 다운로드하여 서버에 저장하는 메소드.
     * 저장된 이미지의 파일 경로를 반환한다.
     *
     * @param imageUrl 다운로드할 이미지의 URL
     * @param title 이미지 파일명에 사용할 제목
     * @return 저장된 이미지 파일의 경로
     * @throws IOException 이미지 다운로드 또는 저장 중 오류 발생 시 예외를 던짐
     */
    private String downloadAndSaveImage(String imageUrl, String title) throws IOException {
        Request request = new Request.Builder().url(imageUrl).build(); // 이미지 URL로 요청 생성
        Response response = client.newCall(request).execute(); // 요청 실행

        if (!response.isSuccessful()) {
            // 응답이 실패하면 예외 발생
            throw new IOException("Failed to download image: " + response);
        }

        // 응답 본문에서 이미지 데이터를 바이트 배열로 읽어옴
        byte[] imageBytes = response.body().bytes();
        response.close();

        // 고유한 파일명을 생성하여 이미지 저장 경로 설정
        String savedName = UUID.randomUUID().toString() + "_" + title + ".png";
        Path savePath = Paths.get(this.savePath, savedName);
        Files.write(savePath, imageBytes); // 이미지를 지정된 경로에 저장

        return savePath.toString(); // 저장된 파일의 경로 반환
    }
}
