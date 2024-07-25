package kr.pianobear.application.service;

import jakarta.mail.MessagingException;
import kr.pianobear.application.dto.LoginRequestDTO;
import kr.pianobear.application.dto.LoginResponseDTO;
import kr.pianobear.application.dto.RegisterRequestDTO;
import kr.pianobear.application.model.EmailAuth;
import kr.pianobear.application.model.FileData;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.repository.RedisRepository;
import kr.pianobear.application.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final FileDataService fileDataService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisRepository redisRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(MemberRepository memberRepository,
                       FileDataService fileDataService,
                       BCryptPasswordEncoder passwordEncoder,
                       EmailService emailService, RedisRepository redisRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.fileDataService = fileDataService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.redisRepository = redisRepository;
        this.jwtUtil = jwtUtil;
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

        // 이름 설정
        member.setName(registerRequestDTO.getName());

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
        member.setRole("ROLE_UNVERIFIED");

        memberRepository.save(member);

        // 마지막: 이메일 인증 전송
        emailService.sendVerificationEmail(registerRequestDTO.getId(),
                registerRequestDTO.getEmail());

        return member;
    }

    @Transactional
    public boolean verifyEmail(String uuid) {
        EmailAuth emailAuth = (EmailAuth) redisRepository.find(uuid);
        if (emailAuth == null) return false;

        String memberId = emailAuth.getMemberId();
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) return false;

        member.get().setAuthEmail(true);
        member.get().setRole("ROLE_USER");
        redisRepository.delete(uuid);

        return true;
    }

    public boolean isUserIdExists(String userId) {
        return memberRepository.existsById(userId);
    }

    public Optional<LoginResponseDTO> login(String id, String password) {
        Optional<Member> member = memberRepository.findById(id);

        if (member.isEmpty()) return Optional.empty();

        if (!passwordEncoder.matches(password, member.get().getPassword()))
            return Optional.empty();

        String accessToken = jwtUtil.createAccessToken(member.get());
        String refreshToken = jwtUtil.createRefreshToken(member.get());

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setAccessToken(accessToken);
        loginResponseDTO.setRefreshToken(refreshToken);

        return Optional.of(loginResponseDTO);
    }

    public Optional<LoginResponseDTO> refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken))
            return Optional.empty();

        String userId = jwtUtil.parseUsername(refreshToken);
        Optional<Member> member = memberRepository.findById(userId);
        if (member.isEmpty())
            return Optional.empty();

        String newAccessToken = jwtUtil.createAccessToken(member.get());
        String newRefreshToken = jwtUtil.createRefreshToken(member.get());

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setAccessToken(newAccessToken);
        loginResponseDTO.setRefreshToken(newRefreshToken);

        return Optional.of(loginResponseDTO);
    }
}
