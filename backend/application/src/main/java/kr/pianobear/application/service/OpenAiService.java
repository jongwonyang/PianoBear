package kr.pianobear.application.service;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenAiService {

    private static final String OPENAI_URL = "https://api.openai.com/v1/images/generations";

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public String generateImage(String title) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("prompt", title + " sheet music cover");
        json.put("n", 1);
        json.put("size", "1024x1024");

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            JSONObject responseJson = new JSONObject(response.body().string());
            return responseJson.getJSONArray("data").getJSONObject(0).getString("url");
        } else {
            throw new IOException("Failed to generate image: " + response.body().string());
        }
    }
}
