package kr.pianobear.application.service;

import jakarta.mail.MessagingException;
import kr.pianobear.application.dto.RegisterRequestDTO;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
import kr.pianobear.application.repository.RedisRepository;
import kr.pianobear.application.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileDataService fileDataService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() throws IOException, MessagingException {
        // Given
        RegisterRequestDTO requestDTO = new RegisterRequestDTO();
        requestDTO.setId("testUser");
        requestDTO.setEmail("test@example.com");
        requestDTO.setName("Test User");
        requestDTO.setGender('M');
        requestDTO.setBirthday(LocalDate.of(2000, 1, 1));
        requestDTO.setPassword("password");
        requestDTO.setStatusMessage("Hello!");

        MultipartFile profilePic = mock(MultipartFile.class);

        when(memberRepository.existsById(anyString())).thenReturn(false);
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        Member member = authService.register(requestDTO, profilePic);

        // Then
        assertNotNull(member);
        assertEquals("testUser", member.getId());
        assertEquals("test@example.com", member.getEmail());
        assertEquals("Test User", member.getName());
        assertEquals('M', member.getGender());
        assertEquals(LocalDate.of(2000, 1, 1), member.getBirthday());
        assertEquals("encodedPassword", member.getPassword());
        assertEquals("Hello!", member.getStatusMessage());
        assertFalse(member.isAuthEmail());
        assertEquals("ROLE_GUEST", member.getRole());

        verify(memberRepository, times(1)).save(any(Member.class));
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void testRegisterDuplicateId() {
        // Given
        RegisterRequestDTO requestDTO = new RegisterRequestDTO();
        requestDTO.setId("testUser");
        requestDTO.setEmail("test@example.com");
        requestDTO.setName("Test User");
        requestDTO.setGender('M');
        requestDTO.setBirthday(LocalDate.of(2000, 1, 1));
        requestDTO.setPassword("password");
        requestDTO.setStatusMessage("Hello!");

        MultipartFile profilePic = mock(MultipartFile.class);

        when(memberRepository.existsById(anyString())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateKeyException.class, () -> {
            authService.register(requestDTO, profilePic);
        });
    }

    @Test
    void testRegisterInvalidGender() {
        // Given
        RegisterRequestDTO requestDTO = new RegisterRequestDTO();
        requestDTO.setId("testUser");
        requestDTO.setEmail("test@example.com");
        requestDTO.setName("Test User");
        requestDTO.setGender('X');
        requestDTO.setBirthday(LocalDate.of(2000, 1, 1));
        requestDTO.setPassword("password");
        requestDTO.setStatusMessage("Hello!");

        MultipartFile profilePic = mock(MultipartFile.class);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(requestDTO, profilePic);
        });
    }

    @Test
    void testRegisterFutureBirthday() {
        // Given
        RegisterRequestDTO requestDTO = new RegisterRequestDTO();
        requestDTO.setId("testUser");
        requestDTO.setEmail("test@example.com");
        requestDTO.setName("Test User");
        requestDTO.setGender('M');
        requestDTO.setBirthday(LocalDate.now().plusDays(1));
        requestDTO.setPassword("password");
        requestDTO.setStatusMessage("Hello!");

        MultipartFile profilePic = mock(MultipartFile.class);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(requestDTO, profilePic);
        });
    }
}
