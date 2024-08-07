package kr.pianobear.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.service.OpenViduService;

@RestController
@RequestMapping("/api/v1/community")
@Tag(name = "Openvidu", description = "Openvidu 소통방 API")
public class CommunityController {
    
	@Autowired
	private OpenViduService openViduService;
	
	
	@PostMapping("/sessions")
    @Operation(summary = "세션 생성 및 생성된 세션 ID 반환")
	public ResponseEntity<String> createSession() {
        try {
            String sessionId = openViduService.createSession();
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

	@PostMapping("/sessions/{sessionId}/disconnect")
    @Operation(summary = "해당 세션에서 접속 종료")
	public ResponseEntity<String> exitSession(@PathVariable String sessionId) {
        try {
            String token = openViduService.exitSession(sessionId);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

	@PostMapping("/sessions/{sessionId}/invite/{invitee}")
    @Operation(summary = " 반환")
	public ResponseEntity<String> inviteFriend(@PathVariable String sessionId, @PathVariable String invitee) {
        try {
            String token = openViduService.createToken(sessionId);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }


	// @PostMapping("/sessions")
    // @Operation(summary = "세션 생성 및 세션 ID 반환")
	// public ResponseEntity<String> initializeSession(@RequestBody(required = false) Map<String, Object> params)
	// 		throws OpenViduJavaClientException, OpenViduHttpException {
	// 	SessionProperties properties = SessionProperties.fromJson(params).build();
	// 	Session session = openvidu.createSession(properties);
	// 	return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
	// }

	// @PostMapping("/sessions/{sessionId}/connections")
    // @Operation(summary = "해당 세션 ID 접속위한 Token 반환")
	// public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId,
	// 		@RequestBody(required = false) Map<String, Object> params)
	// 		throws OpenViduJavaClientException, OpenViduHttpException {
	// 	Session session = openvidu.getActiveSession(sessionId);
	// 	if (session == null) {
	// 		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	// 	}
	// 	ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
	// 	Connection connection = session.createConnection(properties);
	// 	return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
	// }
}
