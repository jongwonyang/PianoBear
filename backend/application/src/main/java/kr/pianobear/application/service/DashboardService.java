package kr.pianobear.application.service;

import kr.pianobear.application.dto.DashboardSummaryDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.model.UserStreak;
import kr.pianobear.application.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DashboardService {

    private final MemberRepository memberRepository;
    private final MusicPracticeService musicPracticeService;

    @Autowired
    public DashboardService(MemberRepository memberRepository, MusicPracticeService musicPracticeService) {
        this.memberRepository = memberRepository;
        this.musicPracticeService = musicPracticeService;
    }

    public Optional<DashboardSummaryDTO> getSummary(String userId) {
        Optional<Member> member = memberRepository.findById(userId);

        if (member.isEmpty()) return Optional.empty();

        UserStreak userStreak = musicPracticeService.getUserStreak(member.get().getId()).orElse(new UserStreak());

        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        summary.setUserId(member.get().getId());
        summary.setUserName(member.get().getName());
        summary.setProfileImage(FileDataService.getDownloadPath(member.get().getProfilePic().getId()));
        summary.setStreak(userStreak.getCurrentStreak());
//        summary.setMost();


        return null;
    }
}
