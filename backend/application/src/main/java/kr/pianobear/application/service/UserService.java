package kr.pianobear.application.service;

import jakarta.transaction.Transactional;
import kr.pianobear.application.dto.FriendDTO;
import kr.pianobear.application.dto.MyInfoDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UserService(MemberRepository memberRepository, RedisTemplate<String, Object> redisTemplate) {
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
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
                FileDataService.getDownloadPath(member.get().getProfilePic()).orElse(null),
                member.get().getStatusMessage(),
                member.get().isAuthEmail(),
                member.get().getRole()
        );

        return Optional.of(myInfoDTO);
    }

    @Transactional
    public Optional<List<FriendDTO>> getOnlineFriends(String userId) {
        Optional<Member> member = memberRepository.findById(userId);

        if (member.isEmpty())
            return Optional.empty();

        Set<Member> friends = member.get().getFriends();

        List<FriendDTO> onlineFriends = new ArrayList<>();
        for (Member friend : friends) {
            if (redisTemplate.opsForValue().get("user:online:" + friend.getId()) != null) {
                FriendDTO friendDTO = FriendDTO.fromMember(friend);
                onlineFriends.add(friendDTO);
            }
        }

        return Optional.of(onlineFriends);
    }
}
