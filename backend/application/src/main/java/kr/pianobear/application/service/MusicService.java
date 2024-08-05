package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.dto.MusicPracticeDTO;
import kr.pianobear.application.model.*;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.repository.MusicPracticeRepository;
import kr.pianobear.application.repository.MusicRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class MusicService {

    private final MusicRepository musicRepository;
    private final MusicPracticeService musicPracticeService;
    private final FileDataService fileDataService;
    private final MemberRepository memberRepository;
    private final MusicPracticeRepository musicPracticeRepository;

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
    public MusicService(MusicRepository musicRepository, MusicPracticeService musicPracticeService, FileDataService fileDataService, MemberRepository memberRepository, MusicPracticeRepository musicPracticeRepository) {
        this.musicRepository = musicRepository;
        this.musicPracticeService = musicPracticeService;
        this.fileDataService = fileDataService;
        this.memberRepository = memberRepository;
        this.musicPracticeRepository = musicPracticeRepository;
    }

    @Transactional
    public MusicDTO uploadPdf(MultipartFile pdfFile) throws IOException {
        System.out.println("Saving PDF file: " + pdfFile.getOriginalFilename());
        FileData fileData = fileDataService.savePdfFile(pdfFile);

        Music music = new Music();
        music.setTitle(pdfFile.getOriginalFilename().replace(".pdf", ""));
        music.setOriginalFileRoute(fileData.getPath());
        MusicHighScore highScore = new MusicHighScore();
        Music savedMusic = null;
        highScore.setMusic(savedMusic);
        highScore.setScore(0);
        savedMusic = musicRepository.save(music);

        return mapMusicToDTO(savedMusic);
    }

    @Transactional
    public MusicDTO convertPdfToMusicXml(int id, boolean useSax) throws IOException, InterruptedException {
        Optional<Music> optionalMusic = musicRepository.findById(id);
        if (!optionalMusic.isPresent()) {
            throw new RuntimeException("Music not found with id " + id);
        }

        Music music = optionalMusic.get();
        File pdfFile = new File(music.getOriginalFileRoute());
        String mxlFilePath = convertPdfToMusicXml(pdfFile);

        String xmlFilePath = extractMusicXml(mxlFilePath);
        if (useSax) {
            modifyMusicXmlSax(xmlFilePath);
        } else {
            modifyMusicXmlDom(xmlFilePath);
        }
        String modifiedMxlFilePath = compressMusicXml(xmlFilePath);

        music.setMusicXmlRoute(mxlFilePath);
        music.setModifiedMusicXmlRoute(modifiedMxlFilePath);
        Music savedMusic = musicRepository.save(music);

        return mapMusicToDTO(savedMusic);
    }

    @Transactional
    public MusicDTO saveMusic(MusicDTO musicDTO, MultipartFile file) throws IOException {
        Optional<Music> optionalMusic = musicRepository.findById(musicDTO.getId());
        if (!optionalMusic.isPresent()) {
            throw new RuntimeException("Music not found with id " + musicDTO.getId());
        }

        Music music = optionalMusic.get();
        music.setTitle(musicDTO.getTitle());
        music.setArtist(musicDTO.getArtist());

        Member user = memberRepository.findById(musicDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id " + musicDTO.getUserId()));
        music.setUser(user);

        music.setUploadDate(LocalDate.now());
        music.setMusicImg(createMusicImg(music.getTitle()));

        Music savedMusic = musicRepository.save(music);

        return mapMusicToDTO(savedMusic);
    }

    private String convertPdfToMusicXml(File pdfFile) throws IOException, InterruptedException {
        String mxlFilePath = pdfFile.getPath().replace(".pdf", ".mxl");

        ProcessBuilder processBuilder = new ProcessBuilder(
                "/app/audiveris",
                "-batch",
                pdfFile.getPath(),
                "-export",
                mxlFilePath
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();

        if (!new File(mxlFilePath).exists()) {
            throw new IOException("Failed to create MusicXML file from PDF");
        }

        return mxlFilePath;
    }

    private String extractMusicXml(String mxlFilePath) throws IOException {
        File mxlFile = new File(mxlFilePath);
        File outputDir = new File(mxlFile.getParentFile(), "extracted");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(mxlFile))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File outputFile = new File(outputDir, entry.getName());
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    IOUtils.copy(zipInputStream, outputStream);
                }
            }
        }

        return new File(outputDir, "main.xml").getPath();
    }

    private void modifyMusicXmlDom(String xmlFilePath) throws IOException {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(new File(xmlFilePath));
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
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new IOException("Failed to modify MusicXML file", e);
        }
    }

    private void modifyMusicXmlSax(String xmlFilePath) throws IOException {
        File modifiedXmlFile = new File(xmlFilePath.replace(".xml", "_modified.xml"));
        try (FileOutputStream fos = new FileOutputStream(modifiedXmlFile);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new File(xmlFilePath), new DefaultHandler() {
                private boolean inPitch = false;
                private boolean inStep = false;
                private String currentStep = null;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    try {
                        bufferedWriter.write("<" + qName);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            bufferedWriter.write(" " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"");
                        }
                        bufferedWriter.write(">");
                    } catch (IOException e) {
                        throw new SAXException(e);
                    }

                    if (qName.equalsIgnoreCase("pitch")) {
                        inPitch = true;
                    } else if (qName.equalsIgnoreCase("step")) {
                        inStep = true;
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    try {
                        if (qName.equalsIgnoreCase("step")) {
                            inStep = false;
                        } else if (qName.equalsIgnoreCase("pitch")) {
                            inPitch = false;
                        }

                        if (qName.equalsIgnoreCase("note") && currentStep != null) {
                            bufferedWriter.write("<lyric><text>" + noteToSyllable.get(currentStep) + "</text></lyric>");
                            currentStep = null;
                        }
                        bufferedWriter.write("</" + qName + ">");
                    } catch (IOException e) {
                        throw new SAXException(e);
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    try {
                        if (inStep && inPitch) {
                            currentStep = new String(ch, start, length);
                        }
                        bufferedWriter.write(new String(ch, start, length));
                    } catch (IOException e) {
                        throw new SAXException(e);
                    }
                }
            });
        } catch (Exception e) {
            throw new IOException("Failed to modify MusicXML file", e);
        }
    }

    private String compressMusicXml(String xmlFilePath) throws IOException {
        File xmlFile = new File(xmlFilePath);
        File outputDir = xmlFile.getParentFile();
        String mxlFilePath = xmlFilePath.replace(".xml", "_modified.mxl");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(mxlFilePath))) {
            File[] files = outputDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                        IOUtils.copy(inputStream, zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                }
            }
        }

        return mxlFilePath;
    }

    private String createMusicImg(String title) {
        // OpenAI API를 사용하여 이미지를 생성하는 로직
        // 생성된 이미지 경로를 반환
        return "/path/to/generated/image.png";
    }

    private MusicDTO mapMusicToDTO(Music music) {
        MusicDTO musicDTO = new MusicDTO();
        musicDTO.setId(music.getId());
        musicDTO.setTitle(music.getTitle());
        musicDTO.setOriginalFileRoute(music.getOriginalFileRoute());
        musicDTO.setMusicXmlRoute(music.getMusicXmlRoute());
        musicDTO.setModifiedMusicXmlRoute(music.getModifiedMusicXmlRoute());
        musicDTO.setUserId(music.getUser().getId());
        musicDTO.setMusicImg(music.getMusicImg());
        musicDTO.setFavorite(music.getFavorite());
        musicDTO.setUploadDate(music.getUploadDate());
        musicDTO.setArtist(music.getArtist());
        return musicDTO;
    }

    public List<MusicDTO> getAllMusic() {
        List<Music> musicList = musicRepository.findAll();
        return musicList.stream().map(this::mapMusicToDTO).collect(Collectors.toList());
    }

    public Optional<MusicDTO> getMusicById(int id) {
        Optional<Music> music = musicRepository.findById(id);
        return music.map(this::mapMusicToDTO);
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
        return musicList.stream().map(this::mapMusicToDTO).collect(Collectors.toList());
    }

    public List<MusicDTO> searchMusicByArtist(String artist) {
        List<Music> musicList = musicRepository.findByArtistContainingIgnoreCase(artist);
        return musicList.stream().map(this::mapMusicToDTO).collect(Collectors.toList());
    }

    public MusicPracticeDTO practiceMusic(int musicId, String userId) {
        return musicPracticeService.practiceMusic(musicId, userId);
    }

    public List<LocalDate> getUploadDates(String userId) {
        List<Music> musicList = musicRepository.findByUserId(userId);
        return musicList.stream().map(Music::getUploadDate).collect(Collectors.toList());
    }

    public boolean getFavoriteStatus(int id) {
        Optional<Music> music = musicRepository.findById(id);
        return music.map(Music::getFavorite).orElse(false);
    }

    public MusicDTO updateMusic(int id, MusicDTO musicDTO) {
        Optional<Music> optionalMusic = musicRepository.findById(id);
        if (optionalMusic.isPresent()) {
            Music music = optionalMusic.get();
            music.setTitle(musicDTO.getTitle());
            music.setArtist(musicDTO.getArtist());
            Music updatedMusic = musicRepository.save(music);
            return mapMusicToDTO(updatedMusic);
        }
        return null;
    }

    public List<MusicDTO> getMusicByUserAndSort(String userId, String sortBy, String direction) {
        List<Music> musicList;

        switch (sortBy.toLowerCase()) {
            case "uploaddate":
                if ("asc".equalsIgnoreCase(direction)) {
                    musicList = musicRepository.findByUserIdOrderByUploadDateAsc(userId);
                } else {
                    musicList = musicRepository.findByUserIdOrderByUploadDateDesc(userId);
                }
                break;
            case "title":
                if ("asc".equalsIgnoreCase(direction)) {
                    musicList = musicRepository.findByUserIdOrderByTitleAsc(userId);
                } else {
                    musicList = musicRepository.findByUserIdOrderByTitleDesc(userId);
                }
                break;
            case "favorite":
                musicList = musicRepository.findByUserIdOrderByFavoriteDesc(userId);
                break;
            case "practicecount":
                musicList = musicPracticeRepository.findTop3ByUserIdOrderByPracticeCountDesc(userId);
                break;
            default:
                throw new IllegalArgumentException("Invalid sort parameter");
        }

        return musicList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MusicDTO> getTop3Practiced(String userId) {
        List<Music> top3PracticedMusic = musicPracticeRepository.findTop3ByUserIdOrderByPracticeCountDesc(userId);
        return top3PracticedMusic.stream()
                .map(music -> mapToDTO(music))
                .collect(Collectors.toList());
    }

    private MusicDTO mapToDTO(Music music) {
        MusicDTO musicDTO = new MusicDTO();
        musicDTO.setId(music.getId());
        musicDTO.setTitle(music.getTitle());
        musicDTO.setOriginalFileRoute(music.getOriginalFileRoute());
        musicDTO.setMusicXmlRoute(music.getMusicXmlRoute());
        musicDTO.setModifiedMusicXmlRoute(music.getModifiedMusicXmlRoute());
        musicDTO.setUserId(music.getUser() != null ? music.getUser().getId() : null);
        musicDTO.setMusicImg(music.getMusicImg());
        musicDTO.setFavorite(music.getFavorite());
        musicDTO.setUploadDate(music.getUploadDate());
        musicDTO.setArtist(music.getArtist());
        return musicDTO;
    }

    @Transactional
    public MusicDTO addDummyMusic(String title, String artist, String userId, String fileRoute, String musicImg, boolean favorite, LocalDate uploadDate) {
        Music music = new Music();
        music.setTitle(title);
        music.setArtist(artist);
        music.setOriginalFileRoute(fileRoute);
        music.setMusicImg(musicImg);
        music.setFavorite(favorite);
        music.setUploadDate(uploadDate);

        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        music.setUser(user);

        Music savedMusic = musicRepository.save(music);
        return mapMusicToDTO(savedMusic);
    }

}
