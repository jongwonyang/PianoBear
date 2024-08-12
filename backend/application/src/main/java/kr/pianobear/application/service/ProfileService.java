package kr.pianobear.application.service;

import jakarta.transaction.Transactional;
import kr.pianobear.application.dto.NameUpdateRequestDTO;
import kr.pianobear.application.dto.PasswordUpdateRequestDTO;
import kr.pianobear.application.model.FileData;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public ProfileService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Optional<NameUpdateRequestDTO> updateName(String id, String newName) {
        Optional<Member> memberOpt = memberRepository.findById(id);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            member.setName(newName);
            memberRepository.save(member);

            NameUpdateRequestDTO nameUpdateRequestDTO = new NameUpdateRequestDTO(
                    member.getId(),
                    member.getName()
            );

            return Optional.of(nameUpdateRequestDTO);
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
                    member.getId(),
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

            // 파일 저장 경로 지정
            String uploadDir = "${file.save-path}";
            String originalFileName = profilePic.getOriginalFilename();
            String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            Path filePath = Paths.get(uploadDir, savedFileName);

            // FileData 객체 생성
            FileData fileData = new FileData();
            fileData.setOriginalName(originalFileName);
            fileData.setSavedName(savedFileName);
            fileData.setType(profilePic.getContentType());
            fileData.setPath(uploadDir);

            // 파일 저장
            Files.createDirectories(filePath.getParent());
            profilePic.transferTo(filePath.toFile());

            // 회원의 프로필 사진 정보 업데이트
            member.setProfilePic(fileData);
            memberRepository.save(member);

            return Optional.of("프로필 사진이 성공적으로 변경되었습니다.");
        } else {
            return Optional.empty();
        }
    }
}
