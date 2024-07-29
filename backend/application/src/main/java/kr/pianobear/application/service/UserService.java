package kr.pianobear.application.service;

import kr.pianobear.application.dto.MyInfoDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final MemberRepository memberRepository;

    @Autowired
    public UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean userIdExists(String userId) {
        return memberRepository.existsById(userId);
    }

    public boolean emailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Optional<MyInfoDTO> getMyInfo(String userId) {
        Optional<Member> member = memberRepository.findById(userId);
        if (member.isEmpty())
            return Optional.empty();

        MyInfoDTO myInfoDTO = new MyInfoDTO(
                member.get().getId(),
                member.get().getEmail(),
                member.get().getName(),
                member.get().getGender(),
                member.get().getBirthday(),
                "/api/v1/files/" + member.get().getProfilePic().getId(),
                member.get().getStatusMessage(),
                member.get().getAuthEmail(),
                member.get().getRole()
        );

        return Optional.of(myInfoDTO);
    }
}
