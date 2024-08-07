package kr.pianobear.application.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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
        Process process = processBuilder.start();
        process.waitFor();

        if (!new File(mxlFilePath).exists()) {
            throw new IOException("Failed to create MusicXML file from PDF");
        }

        return mxlFilePath;
    }
}

