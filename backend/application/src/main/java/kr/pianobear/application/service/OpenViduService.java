package kr.pianobear.application.service;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.Session;
import jakarta.annotation.PostConstruct;
import kr.pianobear.application.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class OpenViduService {

    private OpenVidu openVidu;
    private Map<String, Session> sessions;
    private Map<String, Set<String>> sessionParticipants;

    @Value("${application.openvidu-url}")
	private String OPENVIDU_URL;

	@Value("${application.openvidu-secret}")
	private String OPENVIDU_SECRET;

    private final int MAX_PARTICIPANTS = 6;

    public OpenViduService() {
        this.sessions = new HashMap<>();
        this.sessionParticipants = new HashMap<>();
    }

    @PostConstruct
    private void getOpenVidu() {
        openVidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    public String createSession() throws Exception {
        Session session = openVidu.createSession();
        sessions.put(session.getSessionId(), session);
        sessionParticipants.put(session.getSessionId(), new HashSet<>());
        return session.getSessionId();
    }

    public String enterSession(String sessionId) throws Exception {
        Session session = openVidu.getActiveSession(sessionId);
        if (session == null) {
            sessions.remove(sessionId);
            throw new Exception("Session not found");
        }

        if (sessionParticipants.get(sessionId).size() >= MAX_PARTICIPANTS) {
            throw new Exception("Max participants reached");
        }

        Connection connection = session.createConnection();
        String currentUserId = SecurityUtil.getCurrentUserId();
        sessionParticipants.get(sessionId).add(currentUserId);
        return connection.getConnectionId();
    }

    public String exitSession(String sessionId) throws Exception {
        Session session = openVidu.getActiveSession(sessionId);
        if (session == null) {
            sessions.remove(sessionId);
            throw new Exception("Session not found");
        }

        if (sessionParticipants.get(sessionId).size() >= MAX_PARTICIPANTS) {
            throw new Exception("Max participants reached");
        }

        Connection connection = session.createConnection();
        String currentUserId = SecurityUtil.getCurrentUserId();
        sessionParticipants.get(sessionId).add(currentUserId);
        return connection.getConnectionId();
    }

    
}
