package kr.pianobear.application.util;

import kr.pianobear.application.model.FileData;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.FileDataRepository;
import kr.pianobear.application.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseUtil {

    private final FileDataRepository fileDataRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final String SAMPLE_PATH = "sample-uploads/";
    private final List<String> resourceNames = List.of(
            "183-200x300.jpg",
            "288-200x200.jpg",
            "566-1920x1080.jpg",
            "685-300x200.jpg",
            "740-200x200.jpg",
            "1063-200x200.jpg"
    );

    @Autowired
    public DatabaseUtil(FileDataRepository fileDataRepository, MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.fileDataRepository = fileDataRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initDatabase() {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                try {
                    copyFiles();
                    saveFileData();
                    createMembers();
                    createFriendRelationships();
                    System.out.println("sample data inserted");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void copyFiles() throws IOException {
        Path targetDirectory = Paths.get("/app/data/uploads");

        for (String resourceName : resourceNames) {
            Resource resource = new ClassPathResource(SAMPLE_PATH + resourceName);
            Path targetPath = targetDirectory.resolve(resource.getFilename());
            if (!Files.exists(targetPath)) {  // 파일이 존재하지 않는 경우에만 복사
                try (InputStream inputStream = resource.getInputStream()) {
                    Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        System.out.println("sample uploads copied (or skipped if exists)");
    }

    private void saveFileData() throws IOException {
        Path targetDirectory = Paths.get("/app/data/uploads");

        for (String resourceName : resourceNames) {
            Path targetPath = targetDirectory.resolve(resourceName);
            if (Files.exists(targetPath)) {
                String savedName = targetPath.getFileName().toString();

                // 파일데이터가 이미 데이터베이스에 존재하는지 확인
                if (!fileDataRepository.existsBySavedName(savedName)) {
                    FileData fileData = new FileData();
                    fileData.setOriginalName(resourceName);
                    fileData.setSavedName(savedName);
                    fileData.setType(Files.probeContentType(targetPath));
                    fileData.setPath(targetPath.toString());

                    fileDataRepository.save(fileData);
                }
            }
        }

        System.out.println("sample file data inserted (or skipped if exists)");
    }

    private void createMembers() {
        for (int i = 0; i < resourceNames.size(); i++) {
            String resourceName = resourceNames.get(i);
            FileData profilePic = null;
            if (i < resourceNames.size() - 1) {
                profilePic = fileDataRepository.findBySavedName(resourceName)
                        .orElseThrow(() -> new RuntimeException("Profile picture not found: " + resourceName));
            }

            String memberId = "user" + (i + 1);  // 로그인 아이디로 사용될 id
            if (memberRepository.existsById(memberId)) {
                continue;
            }

            Member member = new Member();
            member.setId(memberId);
            member.setEmail(memberId + "@example.com");
            member.setName("User " + (i + 1));
            member.setGender(i % 2 == 0 ? 'M' : 'F');
            member.setBirthday(LocalDate.of(1990, (i % 12) + 1, (i % 28) + 1));
            member.setPassword(passwordEncoder.encode("1234"));
            member.setProfilePic(profilePic);
            member.setStatusMessage("Hello, I'm User " + (i + 1));
            member.setAuthEmail(true);
            member.setRole("ROLE_MEMBER");

            memberRepository.save(member);
        }

        System.out.println("sample member created (or skipped if exists)");
    }

    protected void createFriendRelationships() {
        List<Member> members = new ArrayList<>();
        memberRepository.findAll().forEach(members::add);
        for (Member member : members) {
            Set<Member> friends = new HashSet<>(members);
            friends.remove(member);  // 자신을 친구 목록에서 제거
            member.setFriends(friends);
            memberRepository.save(member);
        }

        System.out.println("sample friend relationship inserted");
    }
}
