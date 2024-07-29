package kr.pianobear.application.service;

import kr.pianobear.application.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
