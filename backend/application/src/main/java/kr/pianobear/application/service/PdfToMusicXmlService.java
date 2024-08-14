package kr.pianobear.application.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

@Service
public class PdfToMusicXmlService {

    @Value("${file.save-path}")
    private String outputDirectory;

    private static final String AUDIVERIS_PATH = "/app/audiveris/bin/Audiveris";

    public String convertPdfToMusicXml(String pdfPath) throws IOException, InterruptedException {
        String mxlFilePath = Paths.get(outputDirectory, new File(pdfPath).getName().replace(".pdf", ".mxl")).toString();
        System.out.println(mxlFilePath);

        ProcessBuilder processBuilder = new ProcessBuilder(
                AUDIVERIS_PATH, "-batch", pdfPath, "-export", "-output", "/app/data/uploads"
        );
        processBuilder.directory(new File(outputDirectory));

        // 프로세스를 실행하고 표준 출력과 에러 스트림을 비우는 스레드를 추가
        Process process = processBuilder.start();

        // 비동기적으로 표준 출력 스트림과 에러 스트림을 비웁니다.
        Executors.newSingleThreadExecutor().submit(() -> {
            try (var in = process.getInputStream()) {
                in.transferTo(System.out);  // 필요에 따라 System.out을 다른 OutputStream으로 변경 가능
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Executors.newSingleThreadExecutor().submit(() -> {
            try (var err = process.getErrorStream()) {
                err.transferTo(System.err);  // 필요에 따라 System.err을 다른 OutputStream으로 변경 가능
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        int exitCode = process.waitFor();

        if (exitCode != 0 || !new File(mxlFilePath).exists()) {
            throw new IOException("Failed to create MusicXML file from PDF");
        }

        return mxlFilePath;
    }
}