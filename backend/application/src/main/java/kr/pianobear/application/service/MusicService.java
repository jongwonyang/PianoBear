package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicDTO;
import kr.pianobear.application.dto.MusicPracticeDTO;
import kr.pianobear.application.model.*;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.repository.MusicPracticeRepository;
import kr.pianobear.application.repository.MusicRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MusicService {

    private final MusicRepository musicRepository;
    private final MusicPracticeService musicPracticeService;
    private final FileDataService fileDataService;
    private final MemberRepository memberRepository;
    private final MusicPracticeRepository musicPracticeRepository;
    private final PdfToMusicXmlService pdfToMusicXmlService;
    private final MusicXmlModifierService musicXmlModifierService;

    @Autowired
    public MusicService(MusicRepository musicRepository, MusicPracticeService musicPracticeService, FileDataService fileDataService, MemberRepository memberRepository, MusicPracticeRepository musicPracticeRepository, PdfToMusicXmlService pdfToMusicXmlService, MusicXmlModifierService musicXmlModifierService) {
        this.musicRepository = musicRepository;
        this.musicPracticeService = musicPracticeService;
        this.fileDataService = fileDataService;
        this.memberRepository = memberRepository;
        this.musicPracticeRepository = musicPracticeRepository;
        this.pdfToMusicXmlService = pdfToMusicXmlService;
        this.musicXmlModifierService = musicXmlModifierService;
    }

//    @Transactional
//    public MusicDTO processPdfUpload(MultipartFile pdfFile) throws IOException, InterruptedException {
//        // PDF 파일 저장
//        FileData fileData = fileDataService.savePdfFile(pdfFile);
//
//        // 새로운 Music 엔티티 생성 및 초기화
//        Music music = new Music();
//        music.setTitle(pdfFile.getOriginalFilename().replace(".pdf", ""));
//        music.setFavorite(false);
//        music.setUploadDate(LocalDate.now());
//
//        // 현재 사용자 정보 설정
//        String currentUserId = getCurrentUserId();
//        Member user = memberRepository.findById(currentUserId)
//                .orElseThrow(() -> new RuntimeException("User not found with id " + currentUserId));
//        music.setUser(user);
//
//        // PDF to MusicXML 변환
//        String mxlFilePath = pdfToMusicXmlService.convertPdfToMusicXml(fileData.getPath());
//        music.setMusicXmlRoute(mxlFilePath);
//
//        // MusicXML 수정
//        String modifiedXmlFilePath = musicXmlModifierService.modifyMusicXml(mxlFilePath);
//        music.setModifiedMusicXmlRoute(modifiedXmlFilePath);
//
//        // Music 엔티티 저장
//        Music savedMusic = musicRepository.save(music);
//        return mapMusicToDTO(savedMusic);
//    }

    @Transactional
    public MusicDTO processPdfUpload(MultipartFile pdfFile) throws IOException, InterruptedException {
        // PDF 파일 저장
        FileData fileData = fileDataService.savePdfFile(pdfFile);

        // 새로운 Music 엔티티 생성 및 초기화
        Music music = new Music();
        music.setTitle(pdfFile.getOriginalFilename().replace(".pdf", ""));
        music.setFavorite(false);
        music.setUploadDate(LocalDate.now());

        // 현재 사용자 정보 설정
        String currentUserId = getCurrentUserId();
        Member user = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + currentUserId));
        music.setUser(user);

        // PDF to MusicXML 변환
        String mxlFilePath = pdfToMusicXmlService.convertPdfToMusicXml(fileData.getPath());
        music.setMusicXmlRoute(mxlFilePath);

        // MusicXML 수정
        String modifiedMxlFilePath = musicXmlModifierService.modifyMusicXml(mxlFilePath);
        music.setModifiedMusicXmlRoute(modifiedMxlFilePath);

        // Music 엔티티 저장
        Music savedMusic = musicRepository.save(music);
        return mapMusicToDTO(savedMusic);
    }

    public String getModifiedMusicXmlRoute(int musicId) {
        Optional<Music> optionalMusic = musicRepository.findById(musicId);
        if (optionalMusic.isPresent()) {
            Music music = optionalMusic.get();
            return music.getModifiedMusicXmlRoute();
        } else {
            throw new RuntimeException("Music not found with id " + musicId);
        }
    }

    @Transactional
    public MusicDTO saveMusic(MusicDTO musicDTO) {
        Music music = new Music();
        music.setTitle(musicDTO.getTitle());
        music.setArtist(musicDTO.getArtist());
        music.setUploadDate(LocalDate.now());
        music.setFavorite(false);

        String currentUserId = getCurrentUserId();
        Member user = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + currentUserId));
        music.setUser(user);

        music.setMusicXmlRoute(musicDTO.getMusicXmlRoute());
        music.setModifiedMusicXmlRoute(musicDTO.getModifiedMusicXmlRoute());
        music.setMusicImg(createMusicImg(musicDTO.getTitle()));

        Music savedMusic = musicRepository.save(music);

        return mapMusicToDTO(savedMusic);
    }

    private String saveFile(MultipartFile file, String originalFileName) throws IOException {
        String savedName = System.currentTimeMillis() + "_" + originalFileName;
        Path savePath = Paths.get("uploads", savedName);
        Files.createDirectories(savePath.getParent());
        file.transferTo(savePath.toFile());
        return savePath.toString();
    }

    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private String createMusicImg(String title) {
        // 이미지 생성 로직
        return "/path/to/generated/image.png";
    }

    private MusicDTO mapMusicToDTO(Music music) {
        MusicDTO musicDTO = new MusicDTO();
        musicDTO.setId(music.getId());
        musicDTO.setTitle(music.getTitle());
        musicDTO.setMusicXmlRoute(music.getMusicXmlRoute());
        musicDTO.setModifiedMusicXmlRoute(music.getModifiedMusicXmlRoute());
        musicDTO.setUserId(music.getUser().getId());
        musicDTO.setMusicImg(music.getMusicImg());
        musicDTO.setFavorite(music.getFavorite());
        musicDTO.setUploadDate(music.getUploadDate());
        musicDTO.setArtist(music.getArtist());
        return musicDTO;
    }

    public String getMusicImgPath(int musicId) {
        Optional<Music> optionalMusic = musicRepository.findById(musicId);
        if (optionalMusic.isPresent()) {
            Music music = optionalMusic.get();
            return music.getMusicImg();
        } else {
            throw new RuntimeException("Music not found with id " + musicId);
        }
    }

    public String getMusicXmlRoute(int musicId) {
        Optional<Music> optionalMusic = musicRepository.findById(musicId);
        if (optionalMusic.isPresent()) {
            Music music = optionalMusic.get();
            return music.getMusicXmlRoute();
        } else {
            throw new RuntimeException("Music not found with id " + musicId);
        }
    }

//    public String getModifiedMusicXmlRoute(int musicId) {
//        Optional<Music> optionalMusic = musicRepository.findById(musicId);
//        if (optionalMusic.isPresent()) {
//            Music music = optionalMusic.get();
//            return music.getModifiedMusicXmlRoute();
//        } else {
//            throw new RuntimeException("Music not found with id " + musicId);
//        }
//    }

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
        Optional<Music> optionalMusic = musicRepository.findById(id);
        if (optionalMusic.isPresent()) {
            Music music = optionalMusic.get();

            // 연관된 엔티티 삭제 (CascadeType.ALL 설정으로 인해 자동 삭제)
            musicRepository.delete(music);
        } else {
            throw new RuntimeException("Music not found with id " + id);
        }
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
        musicDTO.setMusicXmlRoute(music.getMusicXmlRoute());
        musicDTO.setModifiedMusicXmlRoute(music.getModifiedMusicXmlRoute());
        musicDTO.setUserId(music.getUser() != null ? music.getUser().getId() : null);
        musicDTO.setMusicImg(music.getMusicImg());
        musicDTO.setFavorite(music.getFavorite());
        musicDTO.setUploadDate(music.getUploadDate());
        musicDTO.setArtist(music.getArtist());
        return musicDTO;
    }

}
