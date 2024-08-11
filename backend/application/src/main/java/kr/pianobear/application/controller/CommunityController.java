package kr.pianobear.application.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.dto.CommunitySessionCreationDTO;
import kr.pianobear.application.service.OpenViduService;

@Controller
@RequestMapping("/api/v1/community")
@Tag(name = "Openvidu", description = "Openvidu 소통방 API")
public class CommunityController {

    @Autowired
    private OpenViduService openViduService;

    @PostMapping("/sessions")
    @Operation(summary = "세션 생성 및 생성된 세션 ID 반환")
    public ResponseEntity<String> createSession(@RequestBody CommunitySessionCreationDTO creation) {
        try {
            System.out.println("방생성: " + creation);
            String sessionId = openViduService.createSession(creation);
            return ResponseEntity.ok(sessionId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/sessions/{sessionId}/connect")
    @Operation(summary = "해당 세션 ID 접속위한 Token 반환 및 참여자 등록")
    public ResponseEntity<String> enterSession(@PathVariable String sessionId) {
        try {
            String token = openViduService.enterSession(sessionId);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/sessions/{sessionId}/invite/{invitee}")
    @Operation(summary = "세션ID에 친구 초대")
    public ResponseEntity<String> inviteFriend(@PathVariable String sessionId, @PathVariable String invitee) {
        try {
            openViduService.inviteSession(sessionId, invitee);
            return ResponseEntity.ok("Done");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/sessions/{sessionId}/participants")
    @Operation(summary = "세션ID에 입장 가능한 친구 목록 반환")
    public ResponseEntity<Set<String>> getParticipants(@PathVariable String sessionId) {
        try {
            Set<String> participants = openViduService.getParticipants(sessionId);
            return ResponseEntity.ok(participants);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}
