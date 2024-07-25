package kr.pianobear.application.service;

import jakarta.mail.MessagingException;
import kr.pianobear.application.dto.RegisterRequestDTO;
import kr.pianobear.application.model.FileData;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.MemberRepository;
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

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() throws IOException, MessagingException {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setId("testId");
        dto.setEmail("test@example.com");
        dto.setGender('M');
        dto.setBirthday(LocalDate.of(2000, 1, 1));
        dto.setPassword("password");
        dto.setStatusMessage("Hello");

        MultipartFile profilePic = mock(MultipartFile.class);
        when(memberRepository.existsById(dto.getId())).thenReturn(false);
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPassword");
        when(fileDataService.uploadImage(profilePic, 200, 200)).thenReturn(new FileData());
        doNothing().when(emailService).sendVerificationEmail(dto.getId(), dto.getEmail());

        Member registeredMember = authService.register(dto, profilePic);

        assertNotNull(registeredMember);
        assertEquals(dto.getId(), registeredMember.getId());
        assertEquals(dto.getEmail(), registeredMember.getEmail());
        assertEquals(dto.getGender(), registeredMember.getGender());
        assertEquals(dto.getBirthday(), registeredMember.getBirthday());
        assertEquals("hashedPassword", registeredMember.getPassword());
        assertNotNull(registeredMember.getProfilePic());
        assertEquals(dto.getStatusMessage(), registeredMember.getStatusMessage());
        assertFalse(registeredMember.getAuthEmail());

        verify(memberRepository).existsById(dto.getId());
        verify(memberRepository).existsByEmail(dto.getEmail());
        verify(passwordEncoder).encode(dto.getPassword());
        verify(fileDataService).uploadImage(profilePic, 200, 200);
        verify(emailService).sendVerificationEmail(dto.getId(), dto.getEmail());
    }

    @Test
    void testRegister_DuplicateId() throws IOException, MessagingException {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setId("duplicateId");
        dto.setEmail("test@example.com");
        dto.setGender('M');
        dto.setBirthday(LocalDate.of(2000, 1, 1));
        dto.setPassword("password");

        when(memberRepository.existsById(dto.getId())).thenReturn(true);

        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class, () -> {
            authService.register(dto, null);
        });

        assertEquals("중복된 아이디입니다.", exception.getMessage());
        verify(memberRepository).existsById(dto.getId());
        verify(memberRepository, never()).existsByEmail(dto.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(fileDataService, never()).uploadImage(any(), anyInt(), anyInt());
        verify(emailService, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void testRegister_DuplicateEmail() throws IOException, MessagingException {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setId("testId");
        dto.setEmail("duplicate@example.com");
        dto.setGender('M');
        dto.setBirthday(LocalDate.of(2000, 1, 1));
        dto.setPassword("password");

        when(memberRepository.existsById(dto.getId())).thenReturn(false);
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class, () -> {
            authService.register(dto, null);
        });

        assertEquals("중복된 이메일입니다.", exception.getMessage());
        verify(memberRepository).existsById(dto.getId());
        verify(memberRepository).existsByEmail(dto.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(fileDataService, never()).uploadImage(any(), anyInt(), anyInt());
        verify(emailService, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void testRegister_InvalidGender() throws IOException, MessagingException {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setId("testId");
        dto.setEmail("test@example.com");
        dto.setGender('X');  // Invalid gender
        dto.setBirthday(LocalDate.of(2000, 1, 1));
        dto.setPassword("password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(dto, null);
        });

        assertEquals("성별은 'M' 또는 'F' 입니다.", exception.getMessage());
        verify(memberRepository).existsById(dto.getId());
        verify(memberRepository).existsByEmail(dto.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(fileDataService, never()).uploadImage(any(), anyInt(), anyInt());
        verify(emailService, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void testRegister_InvalidBirthday() throws IOException, MessagingException {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setId("testId");
        dto.setEmail("test@example.com");
        dto.setGender('M');
        dto.setBirthday(LocalDate.of(3000, 1, 1));  // Invalid birthday
        dto.setPassword("password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(dto, null);
        });

        assertEquals("생일은 오늘 이전이어야 합니다.", exception.getMessage());
        verify(memberRepository).existsById(dto.getId());
        verify(memberRepository).existsByEmail(dto.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(fileDataService, never()).uploadImage(any(), anyInt(), anyInt());
        verify(emailService, never()).sendVerificationEmail(any(), any());
    }
}

