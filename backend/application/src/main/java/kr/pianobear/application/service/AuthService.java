package kr.pianobear.application.service;

import kr.pianobear.application.dto.RegisterRequestDTO;
import kr.pianobear.application.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private MemberRepository memberRepository;

    public Map<String, Object> register(RegisterRequestDTO registerRequestDTO) {
        Map<String, Object> result = new HashMap<>();

        // 아이디 중복 체크
        boolean idExists = memberRepository.existsById(registerRequestDTO.getId());
        if (idExists) {
            result.put("message", "중복된 아이디입니다.");
            result.put("success", false);
            return result;
        }

        // 이메일 중복 체크
        boolean emailExists = memberRepository.existsById(registerRequestDTO.getEmail());
        if (emailExists) {
            result.put("message", "중복된 이메일입니다.");
            result.put("success", false);
            return result;
        }

        // 성별 'M' 또는 'F' 체크
        if (!registerRequestDTO.getGender().equals('M') && !registerRequestDTO.getGender().equals('F')) {
            result.put("message", "성별은 'M' 또는 'F' 입니다.");
            result.put("success", false);
            return result;
        }

        // 생일 오늘 이전인지 체크
        if (registerRequestDTO.getBirthday().isAfter(LocalDate.now())) {
            result.put("message", "생일은 오늘 이전이어야 합니다.");
            result.put("success", false);
            return result;
        }

        // TODO: 비밀번호 해싱


        // TODO: 프로필 사진 업로드

        // TODO: 이메일 인증 기본 false

        // TODO: 이메일 인증 전송

        result.put("message", "회원 가입이 성공했습니다. 이메일 인증을 진행해주세요.");
        result.put("success", true);
        return result;
    }
}
