package com.annyang.auth.controller;

import com.annyang.Main;
import com.annyang.auth.config.AuthConfig;
import com.annyang.auth.dto.LoginRequest;
import com.annyang.auth.dto.SignUpRequest;
import com.annyang.member.entity.Member;
import com.annyang.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private SignUpRequest signUpRequest;
    private Member member;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setName("Test User");

        member = new Member(
            signUpRequest.getEmail(),
            passwordEncoder.encode(signUpRequest.getPassword()),
            signUpRequest.getName()
        );
    }

    @Test
    @DisplayName("회원가입을 할 수 있다")
    void signup_Success() throws Exception {
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("회원가입이 완료되었습니다."))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시 실패한다")
    void signup_DuplicateEmail() throws Exception {
        // given
        memberRepository.save(member);

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value("M002"))
            .andExpect(jsonPath("$.error.message").value("Email already exists"));
    }

    @Test
    @DisplayName("로그인에 성공하고 JWT 토큰을 발급받는다")
    void login_Success() throws Exception {
        // given
        memberRepository.save(member);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(signUpRequest.getEmail());
        loginRequest.setPassword(signUpRequest.getPassword());

        // when & then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andExpect(jsonPath("$.data.message").value("로그인 성공"))
            .andExpect(cookie().exists(AuthConfig.Cookie.REFRESH_TOKEN_NAME))
            .andExpect(cookie().httpOnly(AuthConfig.Cookie.REFRESH_TOKEN_NAME, true))
            .andExpect(cookie().secure(AuthConfig.Cookie.REFRESH_TOKEN_NAME, true));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 실패한다")
    void login_WrongPassword() throws Exception {
        // given
        memberRepository.save(member);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(signUpRequest.getEmail());
        loginRequest.setPassword("wrongpassword");

        // when & then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value("A001"))
            .andExpect(jsonPath("$.error.message").value("Unauthorized access"));
    }

    @Test
    @DisplayName("이메일이 비어있으면 400 에러가 발생한다")
    void signup_EmptyEmail() throws Exception {
        SignUpRequest invalidRequest = new SignUpRequest();
        invalidRequest.setEmail("");
        invalidRequest.setPassword("password123");
        invalidRequest.setName("Test User");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호가 8자 미만이면 400 에러가 발생한다")
    void signup_ShortPassword() throws Exception {
        SignUpRequest invalidRequest = new SignUpRequest();
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("123");
        invalidRequest.setName("Test User");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void meSuccess() throws Exception {
        // given
        String email = "test@example.com";
        String name = "Test User";
        Member member = memberRepository.save(new Member(email, passwordEncoder.encode("password123"), name));
        
        // 로그인하여 JWT 토큰 획득
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("password123");
        
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        String accessToken = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.data.accessToken");

        // when & then
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.name").value(name));
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 내 정보 조회 실패")
    void meFailWhenUnauthenticated() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/auth/me"));

        // then
        result.andExpect(status().isUnauthorized());
    }

    private MvcResult performLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(signUpRequest.getEmail());
        loginRequest.setPassword(signUpRequest.getPassword());

        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();
    }

    private String getAccessToken(MvcResult result) throws Exception {
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.accessToken");
    }

    private String getRefreshTokenFromCookie(MvcResult result) {
        return Arrays.stream(result.getResponse().getCookies())
            .filter(cookie -> cookie.getName().equals(AuthConfig.Cookie.REFRESH_TOKEN_NAME))
            .findFirst()
            .map(Cookie::getValue)
            .orElseThrow(() -> new IllegalStateException("Refresh token not found in cookies"));
    }

    @Test
    @DisplayName("리프레시 토큰으로 새로운 액세스 토큰을 발급받고 인증에 성공한다")
    void refresh_Success() throws Exception {
        // given
        memberRepository.save(member);
        MvcResult loginResult = performLogin();
        String refreshToken = getRefreshTokenFromCookie(loginResult);

        // when
        MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
                .cookie(new Cookie(AuthConfig.Cookie.REFRESH_TOKEN_NAME, refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(cookie().exists(AuthConfig.Cookie.REFRESH_TOKEN_NAME))
                .andExpect(cookie().httpOnly(AuthConfig.Cookie.REFRESH_TOKEN_NAME, true))
                .andExpect(cookie().secure(AuthConfig.Cookie.REFRESH_TOKEN_NAME, true))
                .andReturn();

        // then
        String newAccessToken = getAccessToken(refreshResult);
        mockMvc.perform(get("/auth/me")
                .header(AuthConfig.Token.AUTHORIZATION_HEADER, AuthConfig.Token.BEARER_TYPE + " " + newAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(signUpRequest.getEmail()))
                .andExpect(jsonPath("$.data.name").value(signUpRequest.getName()));
    }

    @Test
    @DisplayName("로그아웃 성공 및 이후 인증 실패 확인")
    void logout_Success() throws Exception {
        // given
        memberRepository.save(member);
        MvcResult loginResult = performLogin();
        String accessToken = getAccessToken(loginResult);

        // when & then
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("로그아웃되었습니다."))
                .andExpect(cookie().exists(AuthConfig.Cookie.REFRESH_TOKEN_NAME))
                .andExpect(cookie().maxAge(AuthConfig.Cookie.REFRESH_TOKEN_NAME, 0));
    }
}