package kr.pianobear.application.service;

import kr.pianobear.application.model.MusicPractice;
import kr.pianobear.application.repository.MusicPracticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MusicPracticeService {
    @Autowired
    private MusicPracticeRepository musicPracticeRepository;

    public List<MusicPractice> getPracticeByDay(String userId, int musicId, Date practiceDate) {
        return musicPracticeRepository.findByUserIdAndMusicIdAndPracticeDate(userId, musicId, practiceDate);
    }

    public List<MusicPractice> getPracticeHistory(String userId, int musicId) {
        return musicPracticeRepository.findByUserIdAndMusicIdOrderByPracticeDateAsc(userId, musicId);
    }

    public void addPractice(MusicPractice musicPractice) {
        musicPracticeRepository.save(musicPractice);
    }
}
