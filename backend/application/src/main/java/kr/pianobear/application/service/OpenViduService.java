package kr.pianobear.application.service;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.Session;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import kr.pianobear.application.dto.CommunitySessionCreationDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class OpenViduService {

    private OpenVidu openVidu;

    private Map<String, SessionData> sessionDataMap;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${application.openvidu-url}")
    private String OPENVIDU_URL;

    @Value("${application.openvidu-secret}")
    private String OPENVIDU_SECRET;

    private final int MAX_PARTICIPANTS = 6;

    public OpenViduService() {
        this.sessionDataMap = new HashMap<>();
    }

    @PostConstruct
    private void getOpenVidu() {
        openVidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    public String createSession(CommunitySessionCreationDTO creationData) throws Exception {
        Session session = openVidu.createSession();

        SessionData sessData = new SessionData(creationData.getSessionTitle(), creationData.getInvitationMessage(),
                creationData.getDescription(), new HashSet<>());

        String currentUserId = SecurityUtil.getCurrentUserId();
        sessData.getParticipants().add(currentUserId);

        sessionDataMap.put(session.getSessionId(), sessData);

        return session.getSessionId();
    }

    public String enterSession(String sessionId) throws Exception {
        Session session = openVidu.getActiveSession(sessionId);

        if (session == null) {
            sessionDataMap.remove(sessionId);
            throw new Exception("Session not found");
        }

        if (session.getConnections().size() >= MAX_PARTICIPANTS) {
            throw new Exception("Max participants reached");
        }

        String currentUserId = SecurityUtil.getCurrentUserId();

        if (!sessionDataMap.get(sessionId).getParticipants().contains(currentUserId)) {
            throw new Exception("Not permitted to enter this session");
        }

        Connection connection = session.createConnection();
        return connection.getToken();
    }

    public void inviteSession(String sessionId, String targetUserId) throws Exception {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Optional<Member> currentUserOpt = memberRepository.findById(currentUserId);
        if (currentUserOpt.isEmpty()) {
            throw new Exception("inviter user does not exist.");
        }
        Optional<Member> targetUserOpt = memberRepository.findById(targetUserId);
        if (targetUserOpt.isEmpty()) {
            throw new Exception("invitee user does not exist.");
        }

        Member currentUser = currentUserOpt.get();
        Member targetUser = targetUserOpt.get();

        if (!currentUser.getFriends().contains(targetUser)) {
            throw new Exception("invitee is not your friend.");
        }

        SessionData sessionData = sessionDataMap.get(sessionId);
        sessionData.getParticipants().add(targetUserId);

        String notificationContent = String.format(
                "{\"inviterId\":\"%s\", \"inviterName\":\"%s\", \"sessionTitle\":\"%s\", \"invitationMessage\":\"%s\", \"description\":\"%s\"}",
                currentUser.getId(),
                currentUser.getName(),
                sessionData.getSessionTitle(),
                sessionData.getInvitationMessage(),
                sessionData.getDescription()
        );

        notificationService.createNotification(targetUser, "INVITATION", notificationContent);
    }

    public Set<String> getParticipants(String sessionId) throws Exception {
        SessionData sessData = sessionDataMap.get(sessionId);
        if (sessData == null) {
            throw new Exception("Session not found");
        }
        return sessData.getParticipants();
    }

    @AllArgsConstructor
    @Data
    class SessionData {
        // 방제목
        private String sessionTitle;
        // 초대 메시지
        private String invitationMessage;
        // 설명
        private String description;
        // 해당 세션에 입장이 허가된 사용자 목록
        private Set<String> participants;
    }

}
