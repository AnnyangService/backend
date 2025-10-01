# 프로젝트 이슈 트래킹 문서
이 문서는 프로젝트를 진행하며 마주쳤던 주요 기술적 문제들과 해결 과정을 기록합니다. 

각 이슈는 문제 정의, 원인 분석, 해결책, 배운 점으로 구성되어 있습니다.

## 1: 인증 아키텍처 재설계 (HttpOnly 쿠키 vs Authorization 헤더)

- **라벨**: `Security`, `Architecture`, `Auth`
- **핵심 요약**: XSS와 CSRF 공격을 모두 효과적으로 방어하기 위해, 초기 `HttpOnly` 쿠키 방식의 한계를 파악하고 `Authorization` 헤더와 Refresh Token을 병용하는 하이브리드 방식으로 전환했습니다.

### 1. 문제 상황 (Problem)
초기 기획은 `Authorization` 헤더를 통해 Access Token(이하 AT)을 관리하는 것이었으나, XSS 공격 시 토큰이 탈취될 수 있다는 우려에 따라 AT를 `HttpOnly` 쿠키에 저장하는 방식으로 변경했습니다. 그러나 이 방식이 CSRF 공격에 취약하며, OAuth 리다이렉션 시 `SameSite` 정책 문제로 인증이 실패하는 새로운 문제를 야기했습니다.

### 2. 원인 분석 (Analysis)
ChatGPT의 초기 제안을 교차 검증 없이 적용하여 발생한 문제였습니다. 각 방식의 장단점을 깊이 있게 재검토했습니다.

- `HttpOnly` 쿠키 방식:
    - 장점: 스크립트 접근이 불가능하여 XSS 공격으로부터 AT를 보호할 수 있다.
    - 단점:
        1. CSRF 취약점: 사용자의 의도와 무관하게 요청이 전송될 수 있다. (CSRF 토큰, `SameSite` 설정 등 추가 방어 필요)

        2. SameSite=Strict 정책: 외부 도메인(e.g., OAuth 제공자)에서의 리다이렉션 시 쿠키가 전송되지 않아 인증 흐름이 끊긴다.

- `Authorization` 헤더 방식:
    - 장점: 브라우저가 자동으로 요청에 포함하지 않아 CSRF 공격에 원천적으로 안전하다.
    - 단점: 스크립트로 토큰에 접근 가능하여 XSS 공격 시 탈취 위험이 있다.

### 3. 최종 해결책 (Solution)

두 방식의 장점을 결합하는 "심층 방어(Defense in Depth)" 전략을 채택했습니다.

1. **Access Token (AT)**:
- 저장소: JavaScript 메모리 (변수)에 저장합니다. (Local/Session Storage 사용 X)
- 전송 방식: 모든 API 요청 시 `Authorization: Bearer <token>` 헤더에 담아 전송합니다. (CSRF 방어)
- 보안 강화:
    - CSP (Content Security Policy): 신뢰할 수 없는 스크립트의 실행을 차단하여 XSS 공격을 1차적으로 방어합니다.
    - 짧은 만료 시간: 유효 기간을 30분으로 짧게 설정하여 탈취되더라도 피해를 최소화합니다.

2. **Refresh Token (RT)**:
    - 저장소: `HttpOnly`, `SameSite=Strict` 속성을 가진 보안 쿠키에 저장합니다. (XSS, CSRF 모두 방어)
    - 역할: AT 만료 시, 서버의 재발급 API(/reissue)에 RT를 보내 새로운 AT를 발급받는 데 사용합니다.
    - 보안 강화 (향후 계획): DB에 저장하여 'Token Rotation'을 구현하고, 로그아웃 시 명시적으로 만료 처리합니다.

### 4. 배운 점
보안에는 완벽한 단일 해결책(Silver Bullet)이 없으며, 다양한 위협 시나리오를 고려한 다층적 방어 전략이 중요함을 깨달았습니다. 기술적 조언을 맹신하기보다, 그 근간이 되는 CS 지식(XSS, CSRF, 쿠키 정책 등)을 탐구하고 비판적으로 수용하여 자립적으로 최적의 아키텍처를 설계하는 능력을 길렀습니다.



## 2: 로드밸런서 환경에서 Swagger HTTPS 프로토콜 미인식

- **라벨**: `Infra`, `Spring`, `Bug`
- **핵심 요약**: 로드밸런서(ALB) 뒤에서 HTTPS 요청이 HTTP로 변환되면서 Swagger UI가 올바른 프로토콜을 인식하지 못하는 문제를 Spring Boot의 프록시 헤더 신뢰 설정으로 해결했습니다.

### 1. 문제 상황 (Problem)
서버 앞단에 HTTPS를 적용한 로드밸런서(ALB)를 배치하자, Swagger UI에서 생성되는 모든 API 요청 URL이 `http://`로 표시되어 정상적인 테스트가 불가능했습니다.

### 2. 원인 분석 (Analysis)
로드밸런서가 클라이언트의 HTTPS 요청을 받아 내부적으로는 HTTP로 Spring Boot 애플리케이션에 전달합니다(SSL Termination). 이때 Spring Boot는 실제 요청이 HTTPS로 시작되었음을 인지하지 못합니다. `X-Forwarded-Proto: https`와 같은 프록시 헤더를 신뢰하도록 별도 설정이 필요합니다.

### 3. 최종 해결책 (Solution)
`application.yml`에 다음 설정을 추가하여 Spring Boot가 프록시 헤더를 신뢰하고, 이를 기반으로 올바른 URL을 생성하도록 수정했습니다.

```yaml
# application.yml
server:
  forward-headers-strategy: framework
```

### 4. 배운 점
로드밸런서를 활용한 SSL Termination 아키텍처에서는 애플리케이션이 실제 클라이언트 요청의 프로토콜을 올바르게 인식할 수 있도록 프록시 헤더 신뢰 설정이 필수임을 학습했습니다.

## 3: API 문서 외부 노출에 따른 보안 정책 변경

- **라벨**: `Security`, `Policy`
- **핵심 요약**: API 문서의 무분별한 외부 노출 위험을 인식하고, Spring Security를 통한 접근 제어로 보안을 강화했습니다.

### 1. 문제 상황 (Problem)
초기에는 Swagger UI 문서를 HTML로 빌드하여 GitHub Pages에 공개적으로 호스팅할 계획이었습니다. 하지만 이는 모든 API Endpoint 명세가 외부에 그대로 노출되는 심각한 보안 문제를 야기할 수 있습니다.

### 2. 최종 해결책 (Solution)

#### 내재화
Swagger UI를 Spring Boot 애플리케이션에 포함시켜 서버가 직접 서빙하도록 변경했습니다.

#### 접근 제어
Spring Security 설정을 통해 API 문서 Endpoint (`/swagger-ui/**`, `/v3/api-docs/**`)에 ADMIN 권한을 가진 사용자만 접근할 수 있도록 인증/인가 절차를 추가했습니다.

```java
// SecurityConfig.java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
        .anyRequest().authenticated();
}
```

### 3. 배운 점
개발 편의성과 보안성 사이의 균형을 고려하여, 허가된 인원만 API 문서에 접근할 수 있는 시스템을 구축하는 것이 중요함을 깨달았습니다.

## 4: Spring Security 통합 테스트 시 인증 실패

- **라벨**: `Test`, `Spring`, `Bug`
- **핵심 요약**: 테스트 환경에서 SecurityContext가 비어있어 발생하는 인증 실패를 `@WithMockUser` 어노테이션으로 해결했습니다.

### 1. 문제 상황 (Problem)
인증이 필요한 API에 대해 통합 테스트(`@SpringBootTest`)를 실행할 때, SecurityContext가 비어있어 항상 `401 Unauthorized` 또는 `403 Forbidden` 오류가 발생했습니다.

### 2. 원인 분석 (Analysis)
테스트 환경에서는 `OncePerRequestFilter`를 상속받은 `JwtAuthenticationFilter`가 실제 요청처럼 동작하지 않아 `SecurityContextHolder`에 `Authentication` 객체가 설정되지 않습니다. 따라서 테스트 시에는 수동으로 인증 정보를 주입해주어야 합니다.

### 3. 최종 해결책 (Solution)
`spring-security-test` 의존성이 제공하는 `@WithMockUser` 어노테이션을 사용하여 테스트 메서드 실행 전에 임시 SecurityContext를 생성하고 인증된 사용자를 설정해주었습니다.

```java
@Test
@WithMockUser(roles = "USER") // 'USER' 역할을 가진 가짜 사용자로 SecurityContext를 설정
void getMyInfo_Success() throws Exception {
    // Given
    String expectedEmail = "test@example.com";
    
    // When & Then
    mockMvc.perform(get("/auth/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(expectedEmail));
}
```

### 4. 배운 점
Spring Security 통합 테스트에서는 실제 인증 필터가 동작하지 않으므로, 테스트 전용 어노테이션을 활용하여 SecurityContext를 적절히 설정하는 것이 중요함을 학습했습니다.

## 5: 무중단 배포 후 NoClassDefFoundError 발생

- **라벨**: `Deploy`, `Bug`  
- **핵심 요약**: 실행 중인 프로세스를 종료하지 않고 JAR 파일을 덮어쓰면서 발생한 클래스 로딩 문제를 올바른 배포 순서로 해결했습니다.

### 1. 문제 상황 (Problem)
배포 스크립트를 통해 새로운 JAR 파일을 서버에 덮어쓴 후, 서비스 Health Check는 정상이지만 실제 API(Swagger 포함)에 접근 시 `502 Gateway Timeout`이 발생했습니다. 서버 로그 확인 결과 `java.lang.NoClassDefFoundError`가 간헐적으로 출력되고 있었습니다.

### 2. 원인 분석 (Analysis)
기존에 실행 중이던 Java 프로세스를 종료하지 않고 JAR 파일을 덮어쓰면서 발생한 문제였습니다. 실행 중인 애플리케이션이 이전 버전의 클래스들을 메모리에 로드한 상태에서, 새로운 요청이 들어올 때 변경된 클래스를 찾지 못해 오류가 발생한 것입니다.

### 3. 최종 해결책 (Solution)
배포 스크립트에 새로운 JAR 파일을 전송한 후, 기존 프로세스를 kill하고 새로운 JAR 파일로 애플리케이션을 재시작하는 프로세스를 명확하게 추가했습니다.

```bash
#!/bin/bash
# deploy.sh

# 1. 새로운 JAR 파일 전송
scp app.jar server:/opt/app/

# 2. 기존 프로세스 종료
ssh server "pkill -f 'java.*app.jar' || true"

# 3. 새로운 프로세스 시작
ssh server "cd /opt/app && nohup java -jar app.jar > app.log 2>&1 &"

# 4. Health Check
sleep 30
curl -f http://server:8080/actuator/health
```

### 4. 배운 점
무중단 배포를 구현할 때는 프로세스 생명주기 관리가 매우 중요하며, Blue-Green 배포나 Rolling Update 같은 보다 안전한 배포 전략을 고려해야 함을 깨달았습니다.

## 6: 기타 개선 사항

### ID 생성 방식 변경
**문제**: 추측 가능한 순차적 ID (Long) 사용으로 인한 보안 취약점
**해결**: 정렬 가능한 고유 식별자인 **ULID**를 도입하여 보안성을 강화하고 분산 환경에서의 확장성을 확보했습니다.

```java
// Before
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// After  
@Id
private String id = UlidCreator.getUlid().toString();
```

### Docker 컨테이너 통신
**문제**: 로컬 개발 환경에서 컨테이너 간 통신 실패
**해결**: `localhost` 대신 Docker의 내부 DNS인 `host.docker.internal`을 사용하도록 수정했습니다.

```yaml
# application-local.yml
ai:
  server:
    url: http://host.docker.internal:5000  # 기존: http://localhost:5000
```

### 아키텍처 리팩토링
**개선사항**: 
- `BaseEntity`를 분리하여 공통 필드(id, createdAt, updatedAt) 관리
- 도메인별 패키지 구조를 명확히 하여 코드의 유지보수성과 가독성 향상
- 순환 참조 방지를 위한 의존성 구조 개선

```java
// BaseEntity.java
@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {
    @Id
    private String id = UlidCreator.getUlid().toString();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```