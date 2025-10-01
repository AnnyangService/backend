# 코딩 컨벤션 (Coding Conventions)

이 문서는 Hi-Meow 백엔드 프로젝트의 코딩 표준과 규칙을 정의합니다.

## 목차
- [일반 원칙](#일반-원칙)
- [Java 코딩 스타일](#java-코딩-스타일)
- [패키지 구조](#패키지-구조)
- [클래스 및 인터페이스](#클래스-및-인터페이스)
- [메서드](#메서드)
- [변수 및 상수](#변수-및-상수)
- [어노테이션](#어노테이션)
- [Spring Boot 관련](#spring-boot-관련)
- [데이터베이스](#데이터베이스)
- [예외 처리](#예외-처리)
- [테스트](#테스트)
- [주석](#주석)

## 일반 원칙

### 1. 가독성 우선
- 코드는 작성하는 시간보다 읽는 시간이 더 많습니다
- 명확하고 이해하기 쉬운 코드를 작성합니다
- 간결함보다는 명확함을 우선시합니다

### 2. 일관성 유지
- 프로젝트 전체에서 동일한 스타일을 유지합니다
- 기존 코드의 스타일을 따릅니다

### 3. 의미있는 이름 사용
- 변수, 메서드, 클래스명은 그 목적을 명확히 표현해야 합니다
- 축약어 사용을 지양합니다

## Java 코딩 스타일

### 1. 들여쓰기 및 공백
- **들여쓰기**: 4개의 공백 사용 (탭 사용 금지)
- **줄 길이**: 최대 120자
- **중괄호**: 여는 중괄호는 같은 줄에, 닫는 중괄호는 새로운 줄에

```java
// 올바른 예시
public class DiagnosisController {
    private final DiagnosisService diagnosisService;
    
    public ResponseEntity<ApiResponse<Boolean>> createDiagnosis(
            @Valid @RequestBody PostDiagnosisRequest request) {
        // 구현 내용
        return ResponseEntity.ok(ApiResponse.success(true));
    }
}
```

### 2. 네이밍 컨벤션
- **클래스명**: PascalCase (예: `DiagnosisController`)
- **메서드명**: camelCase (예: `createDiagnosis`)
- **변수명**: camelCase (예: `diagnosisService`)
- **상수명**: UPPER_SNAKE_CASE (예: `MAX_RETRY_COUNT`)
- **패키지명**: lowercase (예: `com.annyang.diagnosis`)

### 3. Import 순서
1. java.* 패키지
2. javax.* 패키지
3. 외부 라이브러리
4. 프로젝트 내부 패키지
5. static import는 마지막

```java
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.annyang.diagnosis.dto.PostDiagnosisRequest;
import com.annyang.diagnosis.service.DiagnosisService;
import com.annyang.global.response.ApiResponse;
```

## 패키지 구조

프로젝트는 다음과 같은 패키지 구조를 따릅니다:

```
com.annyang
├── Main.java                    # 메인 애플리케이션 클래스
├── auth/                        # 인증 관련
│   ├── config/                  # 인증 설정
│   ├── controller/              # 인증 컨트롤러
│   ├── dto/                     # 인증 DTO
│   ├── exception/               # 인증 예외
│   ├── service/                 # 인증 서비스
│   └── token/                   # 토큰 관련
├── [domain]/                    # 도메인별 패키지
│   ├── controller/              # REST 컨트롤러
│   ├── dto/                     # 데이터 전송 객체
│   ├── entity/                  # JPA 엔티티
│   ├── exception/               # 도메인 예외
│   ├── repository/              # 데이터 접근 계층
│   └── service/                 # 비즈니스 로직
└── global/                      # 전역 설정 및 공통 기능
    ├── config/                  # 글로벌 설정
    ├── entity/                  # 공통 엔티티 (BaseEntity 등)
    ├── exception/               # 글로벌 예외 처리
    └── response/                # 공통 응답 형식
```

## 클래스 및 인터페이스

### 1. 클래스 구조 순서
```java
public class ExampleClass {
    // 1. 상수
    private static final String CONSTANT_VALUE = "value";
    
    // 2. 필드
    private final SomeService service;
    
    // 3. 생성자
    public ExampleClass(SomeService service) {
        this.service = service;
    }
    
    // 4. 공개 메서드
    public void publicMethod() {
        // 구현
    }
    
    // 5. 비공개 메서드
    private void privateMethod() {
        // 구현
    }
}
```

### 2. 엔티티 클래스
```java
@Entity
@Table(name = "table_name")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExampleEntity extends BaseEntity {
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChildEntity> children;
    
    // 비즈니스 로직을 위한 생성자
    public ExampleEntity(String name) {
        super();
        this.name = name;
    }
}
```

## 메서드

### 1. 메서드 명명 규칙
- **조회**: `get` 또는 `find` 접두사 사용
- **생성**: `create` 또는 `save` 접두사 사용  
- **수정**: `update` 또는 `modify` 접두사 사용
- **삭제**: `delete` 또는 `remove` 접두사 사용
- **검증**: `validate` 또는 `check` 접두사 사용
- **boolean 반환**: `is`, `has`, `can` 접두사 사용

```java
// 올바른 예시
public PostFirstStepDiagnosisResponse diagnoseFirstStep(PostFirstStepDiagnosisRequest request);
public GetSecondStepDiagnosisResponse getSecondDiagnosis(String id);
public void createSecondStepDiagnosis(PostSecondStepDiagnosisRequest request);
public boolean isValidDiagnosis(String diagnosisId);
```

### 2. 메서드 크기
- 한 메서드는 하나의 책임만 가져야 합니다
- 가능한 한 15-20줄 이내로 작성합니다
- 복잡한 로직은 여러 개의 작은 메서드로 분리합니다

## 변수 및 상수

### 1. 변수 선언
```java
// 올바른 예시
private final DiagnosisService diagnosisService;  // final 키워드 사용
private String imageUrl;                          // 명확한 의미의 변수명

// 피해야 할 예시
private DiagnosisService ds;                      // 축약어 사용
private String url;                               // 모호한 변수명
```

### 2. 상수 정의
```java
public class Constants {
    public static final int MAX_RETRY_COUNT = 3;
    public static final String DEFAULT_IMAGE_FORMAT = "jpg";
    public static final long TOKEN_EXPIRATION_TIME = 3600000L; // 1시간
}
```

## 어노테이션

### 1. 순서
```java
@Entity
@Table(name = "diagnosis_target")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisTarget extends BaseEntity {
    // 구현
}
```

### 2. 컨트롤러 어노테이션
```java
@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {
    // 구현
}
```

## Spring Boot 관련

### 1. 컨트롤러
```java
@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {
    
    private final DiagnosisService diagnosisService;
    
    @PostMapping("/step1")
    public ResponseEntity<ApiResponse<PostFirstStepDiagnosisResponse>> diagnosisFirstStep(
            @Valid @RequestBody PostFirstStepDiagnosisRequest request) {
        PostFirstStepDiagnosisResponse response = diagnosisService.diagnoseFirstStep(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
```

### 2. 서비스
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisService {
    
    private final DiagnosisRepository diagnosisRepository;
    
    @Transactional
    public PostFirstStepDiagnosisResponse diagnoseFirstStep(PostFirstStepDiagnosisRequest request) {
        // 비즈니스 로직 구현
    }
}
```

### 3. DTO
```java
@Getter
@Setter
@NoArgsConstructor
public class PostFirstStepDiagnosisRequest {
    
    @NotBlank(message = "이미지 URL은 필수입니다")
    private String imageUrl;
}
```

#### DTO 네이밍 규칙
- **Request DTO**: `{HttpMethod}{기능명}Request` 형식 사용
  - 예: `PostFirstStepDiagnosisRequest`, `PutMemberUpdateRequest`
- **Response DTO**: `{HttpMethod}{기능명}Response` 또는 `Get{기능명}Response` 형식 사용
  - 예: `PostFirstStepDiagnosisResponse`, `GetSecondStepDiagnosisResponse`
- **HTTP 메서드 접두사를 통해 API 엔드포인트와의 매핑을 명확히 함**

## 데이터베이스

### 1. 엔티티 매핑
```java
@Entity
@Table(name = "diagnosis_target")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisTarget extends BaseEntity {
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @OneToMany(mappedBy = "diagnosisTarget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiagnosisRuleDescription> ruleDescriptions;
}
```

### 2. 테이블 및 컬럼 명명
- **테이블명**: snake_case (예: `diagnosis_target`)
- **컬럼명**: snake_case (예: `created_at`)
- **외래키**: `{참조테이블}_id` (예: `diagnosis_target_id`)

## 예외 처리

### 1. 커스텀 예외
```java
public class DiagnosisNotFoundException extends RuntimeException {
    public DiagnosisNotFoundException(String message) {
        super(message);
    }
}
```

### 2. 전역 예외 처리
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DiagnosisNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleDiagnosisNotFound(DiagnosisNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }
}
```

## 테스트

### 1. 테스트 클래스 명명
- `{클래스명}Test` 형식 사용
- 예: `DiagnosisControllerTest`, `DiagnosisServiceTest`

### 2. 테스트 메서드 명명
```java
@Test
void 진단_첫단계_성공_테스트() {
    // Given
    PostFirstStepDiagnosisRequest request = new PostFirstStepDiagnosisRequest();
    request.setImageUrl("http://example.com/image.jpg");
    
    // When
    PostFirstStepDiagnosisResponse response = diagnosisService.diagnoseFirstStep(request);
    
    // Then
    assertThat(response).isNotNull();
}
```

### 3. 테스트 구조
- **Given-When-Then** 패턴 사용
- 각 섹션을 명확히 구분
- 테스트 데이터는 의미있는 값 사용

## 주석

### 1. 주석 작성 원칙
- 코드로 표현할 수 없는 내용만 주석으로 작성
- **왜(Why)**를 설명하는 주석 작성 (무엇을 하는지는 코드로 표현)
- 임시 코드나 TODO는 명확한 기한과 담당자 명시

### 2. JavaDoc
```java
/**
 * 진단 첫 단계를 수행합니다.
 * 
 * @param request 진단 요청 정보
 * @return 진단 결과 응답
 * @throws DiagnosisException 진단 처리 중 오류 발생 시
 */
public PostFirstStepDiagnosisResponse diagnoseFirstStep(PostFirstStepDiagnosisRequest request) {
    // 구현
}
```

### 3. TODO 주석
```java
// TODO: 2024-10-01, @담당자명 - AI 서버 연동 후 실제 진단 로직으로 교체 필요
private String getMockDiagnosisResult() {
    return "mock result";
}
```

## 체크리스트

코드 작성 완료 후 다음 사항들을 확인하세요:

- [ ] 네이밍 컨벤션을 준수했는가?
- [ ] 메서드가 단일 책임을 가지는가?
- [ ] 적절한 어노테이션을 사용했는가?
- [ ] 예외 처리가 적절히 되어 있는가?
- [ ] 테스트 코드가 작성되어 있는가?
- [ ] 불필요한 주석이나 콘솔 출력이 없는가?
- [ ] Import 문이 정리되어 있는가?

## 참고 자료

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Clean Code by Robert C. Martin](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)