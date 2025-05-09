package com.annyang.member;

import com.annyang.Main;
import com.annyang.member.dto.MemberRequest;
import com.annyang.member.entity.Member;
import com.annyang.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc
@Transactional
class MemberIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MemberRequest memberRequest;
    private Member member;

    @BeforeEach
    void setUp() {
        memberRequest = new MemberRequest("test@example.com", "password123", "홍길동");
        member = new Member(
                memberRequest.email(),
                passwordEncoder.encode(memberRequest.password()),
                memberRequest.name()
        );
    }

    @Test
    @DisplayName("회원을 생성할 수 있다")
    @WithMockUser(username = "test@example.com")
    void createMember() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(memberRequest.email()))
                .andExpect(jsonPath("$.data.name").value(memberRequest.name()));

        Member savedMember = memberRepository.findByEmail(memberRequest.email()).orElseThrow();
        assertThat(savedMember.getEmail()).isEqualTo(memberRequest.email());
        assertThat(savedMember.getName()).isEqualTo(memberRequest.name());
        assertThat(passwordEncoder.matches(memberRequest.password(), savedMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원 생성 시 예외가 발생한다")
    @WithMockUser(username = "test@example.com")
    void createMemberWithDuplicateEmail() throws Exception {
        memberRepository.save(member); // 이미 존재하는 회원 저장

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("M002"))
                .andExpect(jsonPath("$.error.message").value("Email already exists"));
    }

    @Test
    @DisplayName("회원을 조회할 수 있다")
    @WithMockUser(username = "test@example.com")
    void getMember() throws Exception {
        memberRepository.save(member);

        mockMvc.perform(get("/members/{id}", member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(member.getEmail()))
                .andExpect(jsonPath("$.data.name").value(member.getName()));
    }

    @Test
    @DisplayName("존재하지 않는 회원을 조회하면 예외가 발생한다")
    @WithMockUser(username = "test@example.com")
    void getMemberNotFound() throws Exception {
        mockMvc.perform(get("/members/{id}", "01HXSAXEAASYJY0VZ7J2VPHCX8")) // 존재하지 않는 ULID
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("M001"))
                .andExpect(jsonPath("$.error.message").value("Member not found"));
    }

    @Test
    @DisplayName("회원 정보를 수정할 수 있다")
    @WithMockUser(username = "test@example.com")
    void updateMember() throws Exception {
        memberRepository.save(member);

        MemberRequest updateRequest = new MemberRequest(
                member.getEmail(),
                "newpassword123",
                "김철수"
        );

        mockMvc.perform(put("/members/{id}", member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(updateRequest.email()))
                .andExpect(jsonPath("$.data.name").value(updateRequest.name()));
    }

    @Test
    @DisplayName("존재하지 않는 회원 정보를 수정하면 예외가 발생한다")
    @WithMockUser(username = "test@example.com")
    void updateMemberNotFound() throws Exception {
        MemberRequest updateRequest = new MemberRequest("test@example.com", "newpassword123", "김철수");

        mockMvc.perform(put("/members/{id}", "01HXSAXEAASYJY0VZ7J2VPHCX8") // 존재하지 않는 ULID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("M001"))
                .andExpect(jsonPath("$.error.message").value("Member not found"));
    }

    @Test
    @DisplayName("회원을 삭제할 수 있다")
    @WithMockUser(username = "test@example.com")
    void deleteMember() throws Exception {
        memberRepository.save(member);

        mockMvc.perform(delete("/members/{id}", member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(memberRepository.findById(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 회원을 삭제하면 예외가 발생한다")
    @WithMockUser(username = "test@example.com")
    void deleteMemberNotFound() throws Exception {
        mockMvc.perform(delete("/members/{id}", "01HXSAXEAASYJY0VZ7J2VPHCX8")) // 존재하지 않는 ULID
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("M001"))
                .andExpect(jsonPath("$.error.message").value("Member not found"));
    }

    @Test
    @DisplayName("이메일이 비어있으면 400 에러가 발생한다")
    @WithMockUser(username = "test@example.com")
    void createMemberWithEmptyEmail() throws Exception {
        MemberRequest invalidRequest = new MemberRequest("", "password123", "홍길동");

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("G002"));
    }

    @Test
    @DisplayName("비밀번호가 8자 미만이면 400 에러가 발생한다")
    @WithMockUser(username = "test@example.com")
    void createMemberWithShortPassword() throws Exception {
        MemberRequest invalidRequest = new MemberRequest("test@example.com", "123", "홍길동");

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("G002"));
    }
} 