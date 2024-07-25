package kr.pianobear.application.security;

import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findById(username);
        if (member.isEmpty())
            throw new UsernameNotFoundException("해당하는 사용자가 없습니다.");

        return new CustomUserDetails(member.get());
    }

}
