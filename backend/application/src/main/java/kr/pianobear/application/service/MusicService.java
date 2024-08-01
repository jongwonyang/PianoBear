package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.model.FileData;
import kr.pianobear.application.dto.MusicSummaryDTO;
import kr.pianobear.application.dto.MusicPracticeDTO;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MusicService {

    private final MusicRepository musicRepository;
    private final MusicPracticeService musicPracticeService;
    private final UnzipService unzipService;
    private final FileDataService fileDataService;
    private static final Logger logger = LoggerFactory.getLogger(MusicService.class);

    private static final Map<String, String> noteToSyllable = new HashMap<>();

    static {
        noteToSyllable.put("C", "도");
        noteToSyllable.put("D", "레");
        noteToSyllable.put("E", "미");
        noteToSyllable.put("F", "파");
        noteToSyllable.put("G", "솔");
        noteToSyllable.put("A", "라");
        noteToSyllable.put("B", "시");
    }

    @Autowired
    public MusicService(MusicRepository musicRepository, MusicPracticeService musicPracticeService, UnzipService unzipService, FileDataService fileDataService) {
        this.musicRepository = musicRepository;
        this.musicPracticeService = musicPracticeService;
        this.unzipService = unzipService;
        this.fileDataService = fileDataService;
    }

    @Transactional
    public MusicDTO addMusic(MusicDTO musicDTO, MultipartFile pdfFile) throws IOException, InterruptedException {

        FileData uploadedFileData = fileDataService.uploadFile(pdfFile);
        File pdfFileOnDisk = new File(uploadedFileData.getPath());

        String musicXmlPath = convertPdfToMusicXml(pdfFileOnDisk);

        String modifiedMusicXmlPath = modifyMusicXml(musicXmlPath);

        Music music = new Music();
        music.setTitle(musicDTO.getTitle());
        music.setOriginalFileRoute(musicXmlPath);
        music.setChangedFileRoute(modifiedMusicXmlPath);
        music.setPracticeCount(0);
        music.setRecentPractice(null);
        music.setUserId(musicDTO.getUserId());
        music.setMusicImg(musicDTO.getMusicImg());
        music.setFavorite(null);
        music.setUploadDate(LocalDateTime.now());
        music.setArtist(musicDTO.getArtist());
        music.setHighestScore(0);

        Music savedMusic = musicRepository.save(music);
        logger.info("Successfully saved music with title: {}", music.getTitle());
        return mapToDTO(savedMusic);
    }

    private String convertPdfToMusicXml(File pdfFile) throws IOException, InterruptedException {
        logger.info("Converting PDF to MusicXML for file: {}", pdfFile.getPath());
        String musicXmlPath = pdfFile.getPath().replace(".pdf", ".mxl");

        ProcessBuilder processBuilder = new ProcessBuilder(
                "/app/audiveris/bin/Audiveris",
                "-batch",
                pdfFile.getPath(),
                "-export",
                musicXmlPath);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();

        if (!new File(musicXmlPath).exists()) {
            logger.error("Failed to create MusicXML file from PDF: {}", pdfFile.getPath());
            throw new IOException("Failed to create MusicXML file from PDF");
        }

        logger.info("Successfully converted PDF to MusicXML: {}", musicXmlPath);
        return musicXmlPath;
    }

    private String modifyMusicXml(String musicXmlPath) throws IOException {
        logger.info("Modifying MusicXML file: {}", musicXmlPath);
        File tempDir = Files.createTempDirectory("unzippedMusicXml").toFile();
        unzipService.unzipMxlFile(musicXmlPath, tempDir.getAbsolutePath());

        File xmlFile = findMainXmlFile(tempDir);
        if (xmlFile == null) {
            logger.error("No XML file found in the unzipped MusicXML archive: {}", musicXmlPath);
            throw new IOException("No XML file found in the unzipped MusicXML archive");
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList noteList = doc.getElementsByTagName("note");

            for (int i = 0; i < noteList.getLength(); i++) {
                Node note = noteList.item(i);

                if (note.getNodeType() == Node.ELEMENT_NODE) {
                    Element noteElement = (Element) note;

                    NodeList pitchList = noteElement.getElementsByTagName("pitch");
                    if (pitchList.getLength() > 0) {
                        Element pitch = (Element) pitchList.item(0);
                        String step = pitch.getElementsByTagName("step").item(0).getTextContent();
                        String syllable = noteToSyllable.get(step);

                        NodeList lyricList = noteElement.getElementsByTagName("lyric");
                        Element lyric;
                        if (lyricList.getLength() > 0) {
                            lyric = (Element) lyricList.item(0);
                        } else {
                            lyric = doc.createElement("lyric");
                            noteElement.appendChild(lyric);
                        }

                        NodeList textList = lyric.getElementsByTagName("text");
                        Element text;
                        if (textList.getLength() > 0) {
                            text = (Element) textList.item(0);
                        } else {
                            text = doc.createElement("text");
                            lyric.appendChild(text);
                        }

                        text.setTextContent(syllable);
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);
        } catch (Exception e) {
            logger.error("Failed to modify MusicXML file: {}", musicXmlPath, e);
            throw new IOException("Failed to modify MusicXML file", e);
        }

        String modifiedMusicXmlPath = musicXmlPath.replace(".mxl", "_modified.mxl");
        zipMusicXml(tempDir, modifiedMusicXmlPath);

        deleteDirectory(tempDir);

        logger.info("Successfully modified MusicXML file: {}", modifiedMusicXmlPath);
        return modifiedMusicXmlPath;
    }

    private File findMainXmlFile(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    return file;
                }
                if (file.isDirectory()) {
                    File found = findMainXmlFile(file);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

    private void zipMusicXml(File directory, String zipFilePath) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            File[] files = directory.listFiles();
            assert files != null;
            for (File file : files) {
                zipFile(file, file.getName(), zipOut);
            }
        }
    }

    private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            assert children != null;
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }

    public List<MusicDTO> getAllMusic() {
        List<Music> musicList = musicRepository.findAll();
        return musicList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<MusicDTO> getMusicById(int id) {
        Optional<Music> music = musicRepository.findById(id);
        return music.map(this::mapToDTO);
    }

    @Transactional
    public void deleteMusic(int id) {
        musicRepository.deleteById(id);
    }

    @Transactional
    public void favoriteMusic(int id, boolean favorite) {
        Optional<Music> music = musicRepository.findById(id);
        if (music.isPresent()) {
            Music m = music.get();
            m.setFavorite(favorite);
            musicRepository.save(m);
        }
    }

    public List<MusicDTO> searchMusicByTitle(String title) {
        List<Music> musicList = musicRepository.findByTitleContainingIgnoreCase(title);
        return musicList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MusicDTO> searchMusicByArtist(String artist) {
        List<Music> musicList = musicRepository.findByArtistContainingIgnoreCase(artist);
        return musicList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Page<MusicSummaryDTO> getPaginatedMusic(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Music> musicPage = musicRepository.findAll(pageable);
        return musicPage.map(this::mapToSummaryDTO);
    }

    public MusicPracticeDTO practiceMusic(int musicId, String userId) {
        return musicPracticeService.practiceMusic(musicId, userId);
    }

    private MusicDTO mapToDTO(Music music) {
        MusicDTO musicDTO = new MusicDTO();
        musicDTO.setId(music.getId());
        musicDTO.setTitle(music.getTitle());
        musicDTO.setOriginalFileRoute(music.getOriginalFileRoute());
        musicDTO.setChangedFileRoute(music.getChangedFileRoute());
        musicDTO.setPracticeCount(music.getPracticeCount());
        musicDTO.setRecentPractice(music.getRecentPractice() != null ? music.getRecentPractice() : null);
        musicDTO.setUserId(music.getUserId());
        musicDTO.setMusicImg(music.getMusicImg());
        musicDTO.setFavorite(music.getFavorite());
        musicDTO.setUploadDate(music.getUploadDate().toString());
        musicDTO.setArtist(music.getArtist());
        musicDTO.setHighestScore(music.getHighestScore());
        return musicDTO;
    }


    private MusicSummaryDTO mapToSummaryDTO(Music music) {
        MusicSummaryDTO summaryDTO = new MusicSummaryDTO();
        summaryDTO.setMusicImg(music.getMusicImg());
        summaryDTO.setTitle(music.getTitle());
        summaryDTO.setArtist(music.getArtist());
        summaryDTO.setFavorite(music.getFavorite());
        return summaryDTO;
    }

    public List<Music> getTop3Practiced(String userId) {
        return musicRepository.findTop3ByUserIdOrderByPracticeCountDesc(userId);
    }
}
