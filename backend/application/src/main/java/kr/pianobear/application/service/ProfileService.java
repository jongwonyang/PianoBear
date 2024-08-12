package kr.pianobear.application.service;

import jakarta.transaction.Transactional;
import kr.pianobear.application.dto.PasswordUpdateRequestDTO;
import kr.pianobear.application.model.FileData;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ProfileService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileDataService fileDataService;

    @Autowired
    public ProfileService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder, FileDataService fileDataService){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileDataService = fileDataService;
    }

    @Transactional
    public Optional<String> updateName(String id, String newName) {
        Optional<Member> memberOpt = memberRepository.findById(id);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            member.setName(newName);
            memberRepository.save(member);

            return Optional.of(newName);
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<PasswordUpdateRequestDTO> updatePassword(String id, String oldPassword, String newPassword) {
        Optional<Member> memberOpt = memberRepository.findById(id);

        if(memberOpt.isPresent()) {
            Member member = memberOpt.get();

            if(!passwordEncoder.matches(oldPassword, member.getPassword())){
                throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
            }

            member.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(member);

            PasswordUpdateRequestDTO passwordUpdateRequestDTO = new PasswordUpdateRequestDTO(
                    member.getPassword()
            );

            return Optional.of(passwordUpdateRequestDTO);
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<String> updatePhoto(String id, MultipartFile profilePic) throws IOException {
        Optional<Member> memberOpt = memberRepository.findById(id);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            FileData fileData = fileDataService.uploadImage(profilePic, 200, 200);
            member.setProfilePic(fileData);
            memberRepository.save(member);

            return Optional.of("프로필 사진이 성공적으로 변경되었습니다.");
        } else {
            return Optional.empty();
        }
    }
}
