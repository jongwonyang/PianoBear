package kr.pianobear.application.service;

import jakarta.mail.MessagingException;
import kr.pianobear.application.dto.RegisterRequestDTO;
import kr.pianobear.application.model.FileData;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final FileDataService fileDataService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public AuthService(MemberRepository memberRepository,
                       FileDataService fileDataService,
                       BCryptPasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.memberRepository = memberRepository;
        this.fileDataService = fileDataService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public Member register(RegisterRequestDTO registerRequestDTO, MultipartFile profilePic) throws IOException, MessagingException {
        Member member = new Member();

        // 아이디 중복 체크
        boolean idExists = memberRepository.existsById(registerRequestDTO.getId());
        if (idExists) {
            throw new DuplicateKeyException("중복된 아이디입니다.");
        }
        member.setId(registerRequestDTO.getId());

        // 이메일 중복 체크
        boolean emailExists = memberRepository.existsByEmail(registerRequestDTO.getEmail());
        if (emailExists) {
            throw new DuplicateKeyException("중복된 이메일입니다.");
        }
        member.setEmail(registerRequestDTO.getEmail());

        // 성별 'M' 또는 'F' 체크
        if (!registerRequestDTO.getGender().equals('M') && !registerRequestDTO.getGender().equals('F')) {
            throw new IllegalArgumentException("성별은 'M' 또는 'F' 입니다.");
        }
        member.setGender(registerRequestDTO.getGender());

        // 생일 오늘 이전인지 체크
        if (registerRequestDTO.getBirthday().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("생일은 오늘 이전이어야 합니다.");
        }
        member.setBirthday(registerRequestDTO.getBirthday());

        // 비밀번호 해싱
        member.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));

        // 프로필 사진 업로드
        if (profilePic != null) {
            FileData saved = fileDataService.uploadImage(profilePic, 200, 200);
            member.setProfilePic(saved);
        }

        // 상태 메시지 설정
        if (registerRequestDTO.getStatusMessage() != null) {
            member.setStatusMessage(registerRequestDTO.getStatusMessage());
        }

        // 이메일 인증 기본 false
        member.setAuthEmail(false);

        // 권한 설정
        member.setRole("ROLE_USER");

        memberRepository.save(member);

        // 마지막: 이메일 인증 전송
        emailService.sendVerificationEmail(registerRequestDTO.getId(),
                registerRequestDTO.getEmail());

        return member;
    }
}
