# 피아노베어

| 항목          | 내용 |
|--------------|------|
| 🕒 기간      | 2024-07 ~ 2024-08 |
| 👥 인원      | 6명 |
| 🛠 사용 기술 | <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/> <img alt="Spring" src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white" alt="Spring"/> <img src="https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white" alt="Postgres"/> <img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/> <img src="https://img.shields.io/badge/vuejs-%2335495e.svg?style=for-the-badge&logo=vuedotjs&logoColor=%234FC08D" alt="Vue.js"/> <img src="https://img.shields.io/badge/TensorFlow-%23FF6F00.svg?style=for-the-badge&logo=TensorFlow&logoColor=white" alt="TensorFlow"/> <img src="https://img.shields.io/badge/FastAPI-005571?style=for-the-badge&logo=fastapi" alt="FastAPI"/> |
| 🎯 담당 역할 | 1) Spring Security와 JWT를 이용한 회원 인증, 인가 기능 개발 (백엔드)<br/>2) 채보(transcriber) 기능 개발 (AI, 백엔드, 프론트엔드) |
| 📖 개요      | 어린이들을 위한 피아노 학습 도우미 웹 서비스 |

# 프로젝트 개요
## 배경 및 목표
많은 아이들이 피아노 학원을 다니며 피아노를 배웁니다. 하지만 흥미 유발이 쉽지 않고 아이들에게 악보를 읽는것은 어렵게 느껴질 수 있습니다.

이를 위해 읽기 쉬운 악보를 만들어주고 다양한 기능들로 피아노 연습을 재미있게 해주는 웹 서비스를 제작하였습니다.

![피아노배어](img/pianobear-banner.png)

## 주요 기능
### 보기 쉬운 악보 생성
- PDF 형식의 일반 악보를 업로드하면, 계이름을 붙여서 읽기 쉬운 악보로 변환해줍니다.

![쉬운 악보 생성](img/pianobear-easy-sheet.png)

https://github.com/user-attachments/assets/c7f6722c-82a0-42dc-ac72-4c3a5d1b7bf6

- 악보 업로드 후 AI를 이용해 흥미를 유발할만한 커버 이미지를 생성할 수 있습니다.

![커버 이미지 생성](img/pianobear-cover-gen.png)

https://github.com/user-attachments/assets/9b3995ac-5148-48f2-9034-9111c589acb9

### 음원으로 악보 생성

- 원하는 노래의 음원을 업로드하면 피아노 연주만 추출하여 악보를 생성해줍니다.

https://github.com/user-attachments/assets/36deee6a-8142-4751-a407-cf032f47a560

- 피아노를 제외한 음원을 추출하여 내가 피아노 연주자가 되어 합주를 해볼 수 있습니다.

https://github.com/user-attachments/assets/aeecd583-8af4-4384-ab71-7913f90a1e3e

### 연습, 채점

- 생성된 쉬운 악보를 보며 피아노를 연주할 수 있습니다.

https://github.com/user-attachments/assets/15745ed0-d5ee-4f91-8379-32fadd9fbbd7

- 내 연주를 녹음하여 점수를 측정할 수 있습니다.

https://github.com/user-attachments/assets/c402e797-e1bc-4bb8-81e2-6014b69d83ef

### 놀이터 (화상 연주)
- 화상 채팅을 통해 친구들과 소통하며 재미있게 연주할 수 있습니다.

https://github.com/user-attachments/assets/a06c1915-94e4-4139-a458-4808c2c77785

# 나의 기여
## Spring Security와 JWT를 이용한 회원 인증 기능
### 상황
클라이언트가 Vue.js 기반의 SPA이기 때문에 토큰을 이용한 회원 기능을 구현하였습니다.

회원 기능은 대부분의 기능에서 광범위하게 사용되기 때문에 동료 백엔드 개발자가 인증이 필요한 부분에서 쉽고 간단하게 인증 정보를 가져올 수 있도록 작성하는것을 목표로 하였습니다.

### Spring Security 설정

```java
// https://github.com/jongwonyang/PianoBear/blob/master/backend/application/src/main/java/kr/pianobear/application/config/SecurityConfig.java

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private static final String[] AUTH_WHITELIST = {"/api/v1/auth/**"};

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.addFilterBefore(new JwtAuthFilter(jwtUtil, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

위 설정에서는 CSRF 및 CORS 보호를 비활성화하고, 세션을 사용하지 않는 `STATELESS` 정책을 적용하였습니다. 또한, `JwtAuthFilter`를 추가하여 모든 요청에서 JWT 검증을 수행합니다.

### JWTUtil 클래스

```java
// https://github.com/jongwonyang/PianoBear/blob/master/backend/application/src/main/java/kr/pianobear/application/util/JwtUtil.java

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;
    private final CustomUserDetailsService userDetailsService;

    public JwtUtil(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.access-expiration-time}") long accessTokenExpTime,
                   @Value("${jwt.refresh-expiration-time}") long refreshTokenExpTime,
                   CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public String createAccessToken(Member member) {
        return createToken(member, accessTokenExpTime);
    }

    private String createToken(Member member, long expireTime) {
        Claims claims = Jwts.claims()
                .add("username", member.getId())
                .add("role", member.getRole())
                .build();

        ZonedDateTime now = ZonedDateTime.now();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(now.plusSeconds(expireTime).toInstant()))
                .signWith(this.secretKey)
                .compact();
    }

    public String parseUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
```

JWT를 생성하고 검증하는 역할을 수행하는 `JwtUtil` 클래스입니다. `createAccessToken()` 메서드를 통해 JWT를 생성하고, `validateToken()` 메서드에서 토큰이 유효한지 검사합니다.

### JWTAuthFilter 필터

```java
// https://github.com/jongwonyang/PianoBear/blob/master/backend/application/src/main/java/kr/pianobear/application/security/JwtAuthFilter.java

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.parseUsername(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

이 필터는 요청이 들어올 때마다 JWT를 검증하고, 인증된 사용자인 경우 `SecurityContextHolder`에 저장하여 이후 요청에서 사용될 수 있도록 합니다.

### SecurityUtil 클래스

```java
// https://github.com/jongwonyang/PianoBear/blob/master/backend/application/src/main/java/kr/pianobear/application/util/SecurityUtil.java

public class SecurityUtil {
    private SecurityUtil() {}

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }
}
```

이 유틸리티 클래스는 SecurityContextHolder에서 현재 인증된 사용자의 ID를 쉽게 가져올 수 있도록 도와줍니다.

사용 예시는 다음과 같습니다.

```java
// https://github.com/jongwonyang/PianoBear/blob/master/backend/application/src/main/java/kr/pianobear/application/controller/UserController.java

@GetMapping("/my-info")
@Operation(summary = "내 정보 조회")
@PreAuthorize("hasRole('ROLE_MEMBER')")
public ResponseEntity<MyInfoDTO> myInfo() {
    String currentUserId = SecurityUtil.getCurrentUserId();

    Optional<MyInfoDTO> myInfo = userService.getMyInfo(currentUserId);

    if (myInfo.isEmpty())
        return ResponseEntity.notFound().build();

    return ResponseEntity.ok(myInfo.get());
}
```

### 결과
- 위와 같이 Spring Security와 JWT를 활용하여 회원 인증을 구현하였습니다.
- `JwtAuthFilter`를 통해 JWT를 검증하며, `JwtUtil`을 통해 토큰을 생성 및 관리합니다.
- `SecurityUtil`을 추가하여 인증이 필요한 기능에서 쉽게 현재 사용자 정보를 가져올 수 있도록 구현하였습니다.

## 회원 기능의 보안 및 성능 문제
