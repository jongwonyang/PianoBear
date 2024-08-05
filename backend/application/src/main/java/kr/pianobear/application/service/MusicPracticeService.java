package kr.pianobear.application.service;

import kr.pianobear.application.dto.MusicPracticeDTO;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.model.MusicPractice;
import kr.pianobear.application.model.UserStreak;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.repository.MusicPracticeRepository;
import kr.pianobear.application.repository.MusicRepository;
import kr.pianobear.application.repository.UserStreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MusicPracticeService {

    private final MusicPracticeRepository musicPracticeRepository;
    private final MusicRepository musicRepository;
    private final UserStreakRepository userStreakRepository;
    private final MemberRepository memberRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public MusicPracticeService(MusicPracticeRepository musicPracticeRepository, MusicRepository musicRepository, UserStreakRepository userStreakRepository, MemberRepository memberRepository) {
        this.musicPracticeRepository = musicPracticeRepository;
        this.musicRepository = musicRepository;
        this.userStreakRepository = userStreakRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MusicPracticeDTO practiceMusic(int musicId, String userId) {
        Optional<Music> optionalMusic = musicRepository.findById(musicId);
        if (!optionalMusic.isPresent()) {
            throw new RuntimeException("Music not found with id " + musicId);
        }

        Music music = optionalMusic.get();
        musicRepository.save(music);

        LocalDate today = LocalDate.from(LocalDate.now().atStartOfDay());

        Optional<MusicPractice> optionalMusicPractice = musicPracticeRepository.findByMusicAndMemberIdAndPracticeDate(music, userId, today.atStartOfDay());
        MusicPractice musicPractice;
        if (optionalMusicPractice.isPresent()) {
            musicPractice = optionalMusicPractice.get();
            musicPractice.setPracticeCount(musicPractice.getPracticeCount() + 1);
        } else {
            musicPractice = new MusicPractice();
            musicPractice.setMusic(music);
            musicPractice.setMember(new Member(userId));
            musicPractice.setPracticeDate(today);
            musicPractice.setPracticeCount(1);
        }

        MusicPractice savedPractice = musicPracticeRepository.save(musicPractice);

        updateStreak(savedPractice);

        return mapToDTO(savedPractice);
    }

    private void updateStreak(MusicPractice practice) {
        UserStreak streak = userStreakRepository.findById(practice.getMember().getId())
                .orElse(new UserStreak(practice.getMember().getId()));

        LocalDate practiceDate = practice.getPracticeDate();
        if (streak.getLastPracticedDate() != null && practiceDate.minusDays(1).equals(streak.getLastPracticedDate())) {
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        } else {
            streak.setCurrentStreak(1);
        }

        if (streak.getCurrentStreak() > streak.getMaxStreak()) {
            streak.setMaxStreak(streak.getCurrentStreak());
        }

        streak.setLastPracticedDate(practiceDate);
        userStreakRepository.save(streak);
    }

    public Optional<UserStreak> getUserStreak(String userId) {
        return userStreakRepository.findById(userId);
    }

    public List<MusicPracticeDTO> getPracticeDataByUserAndMusic(int musicId, String userId) {
        List<MusicPractice> practiceData = musicPracticeRepository.findByMemberIdAndMusic(userId, new Music(musicId));
        return practiceData.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private MusicPracticeDTO mapToDTO(MusicPractice musicPractice) {
        MusicPracticeDTO dto = new MusicPracticeDTO();
        dto.setId(musicPractice.getId());
        dto.setPracticeDate(musicPractice.getPracticeDate()); // LocalDate로 설정
        dto.setPracticeCount(musicPractice.getPracticeCount());
        dto.setMusicId(musicPractice.getMusic().getId());
        dto.setUserId(musicPractice.getMember().getId());
        return dto;
    }

    public List<MusicPracticeDTO> getAllPracticeDataSortedByDate(int musicId) {
        List<MusicPractice> practiceData = musicPracticeRepository.findByMusicOrderByPracticeDateAsc(new Music(musicId));
        return practiceData.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MusicPracticeDTO> getMonthlyPracticeRecords(String userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        return musicPracticeRepository
                .findAllByMemberIdAndPracticeDateBetween(userId, startDate, endDate)
                .stream()
                .map(MusicPracticeDTO::fromMusicPractice)
                .toList();
    }

    public List<MusicPracticeDTO> getDailyPracticeRecords(String userId, int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

        return musicPracticeRepository
                .findAllByMemberIdAndPracticeDateBetween(userId, startOfDay, endOfDay)
                .stream()
                .map(MusicPracticeDTO::fromMusicPractice)
                .toList();
    }
}
