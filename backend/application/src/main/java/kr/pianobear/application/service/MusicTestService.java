package kr.pianobear.application.service;

import kr.pianobear.application.model.MusicTest;
import kr.pianobear.application.repository.MusicTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicTestService {
    @Autowired
    private MusicTestRepository musicTestRepository;

    public List<MusicTest> getTestsByUser(String userId) {
        return musicTestRepository.findByUserId(userId);
    }

    public void addTest(MusicTest musicTest) {
        musicTestRepository.save(musicTest);
    }
}
