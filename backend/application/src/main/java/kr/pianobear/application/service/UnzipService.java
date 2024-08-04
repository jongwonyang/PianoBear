package kr.pianobear.application.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class UnzipService {

    public void unzipMxlFile(String filePath, String extractTo) throws IOException {
        if (!filePath.endsWith(".mxl")) {
            throw new IllegalArgumentException("The file is not an .mxl file");
        }

        if (extractTo == null || extractTo.isEmpty()) {
            extractTo = filePath.substring(0, filePath.lastIndexOf('.'));
        }

        Files.createDirectories(Paths.get(extractTo));

        try (InputStream is = Files.newInputStream(Paths.get(filePath));
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(extractTo, entry.getName());

                if (entry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}
