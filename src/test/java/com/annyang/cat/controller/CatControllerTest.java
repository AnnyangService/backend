package com.annyang.cat.controller;

import com.annyang.cat.dto.CatRegisterRequest;
import com.annyang.cat.entity.Cat;
import com.annyang.cat.entity.Gender;
import com.annyang.cat.service.CatService;
import com.annyang.Main;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc
@Transactional
class CatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CatService catService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private CatRegisterRequest catRegisterRequest;

    private static final String USER_ID = "01HXSAXEAASYJY0VZ7J2VPHCX8";

    @BeforeEach
    void setUp() {
        // 테스트용 회원 생성
        testMember = new Member(
            "test@example.com",
            passwordEncoder.encode("password123"),
            "Test User"
        );
        ReflectionTestUtils.setField(testMember, "id", USER_ID);
        memberRepository.save(testMember);

        // 테스트용 고양이 등록 요청 데이터 생성
        catRegisterRequest = new CatRegisterRequest();
        catRegisterRequest.setName("Test Cat");
        catRegisterRequest.setBirthDate(LocalDate.of(2020, 1, 1));
        catRegisterRequest.setBreed("Persian");
        catRegisterRequest.setGender(Gender.MALE);
        catRegisterRequest.setWeight(4.0);
        catRegisterRequest.setLastDiagnosis(LocalDate.of(2023, 1, 1));
        catRegisterRequest.setSpecialNotes("Test note");
    }

    @Test
    @DisplayName("고양이를 등록할 수 있다")
    @WithMockUser(username = USER_ID)
    void createCat_Success() throws Exception {
        mockMvc.perform(post("/cats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(catRegisterRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("Test Cat"))
            .andExpect(jsonPath("$.data.breed").value("Persian"));
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 고양이를 등록할 수 없다")
    void createCat_Unauthorized() throws Exception {
        mockMvc.perform(post("/cats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(catRegisterRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("필수 필드가 누락된 경우 고양이를 등록할 수 없다")
    @WithMockUser(username = USER_ID)
    void createCat_ValidationFail() throws Exception {
        catRegisterRequest.setName(null);
        catRegisterRequest.setBreed(null);

        mockMvc.perform(post("/cats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(catRegisterRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("G002"));
    }

    @Test
    @DisplayName("본인의 고양이 목록을 조회할 수 있다")
    @WithMockUser(username = USER_ID)
    void getCurrentMemberCats_Success() throws Exception {
        // 고양이 등록
        Cat savedCat = catService.createCat(catRegisterRequest);

        mockMvc.perform(get("/cats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].name").value("Test Cat"));
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 고양이 목록을 조회할 수 없다")
    void getCurrentMemberCats_Unauthorized() throws Exception {
        mockMvc.perform(get("/cats"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("고양이 상세 정보를 조회할 수 있다")
    @WithMockUser(username = USER_ID)
    void getCat_Success() throws Exception {
        // 고양이 등록
        Cat savedCat = catService.createCat(catRegisterRequest);

        mockMvc.perform(get("/cats/{id}", savedCat.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("Test Cat"))
            .andExpect(jsonPath("$.data.breed").value("Persian"));
    }

    @Test
    @DisplayName("존재하지 않는 고양이를 조회할 수 없다")
    @WithMockUser(username = USER_ID)
    void getCat_NotFound() throws Exception {
        mockMvc.perform(get("/cats/{id}", "non-existent-id"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("고양이 정보를 수정할 수 있다")
    @WithMockUser(username = USER_ID)
    void updateCat_Success() throws Exception {
        // 고양이 등록
        Cat savedCat = catService.createCat(catRegisterRequest);

        // 수정할 데이터 준비
        catRegisterRequest.setName("Updated Cat");
        catRegisterRequest.setWeight(5.0);

        mockMvc.perform(put("/cats/{id}", savedCat.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(catRegisterRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("Updated Cat"))
            .andExpect(jsonPath("$.data.weight").value(5.0));
    }

    @Test
    @DisplayName("고양이를 삭제할 수 있다")
    @WithMockUser(username = USER_ID)
    void deleteCat_Success() throws Exception {
        // 고양이 등록
        Cat savedCat = catService.createCat(catRegisterRequest);

        mockMvc.perform(delete("/cats/{id}", savedCat.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
} 