package com.annyang.chatbot.entity;

import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.annyang.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chatbot_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatbotSession extends BaseEntity {

    public enum SessionType {
        GENERAL, DIAGNOSIS
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "third_step_diagnosis_id", nullable = true, foreignKey = @ForeignKey(name = "fk_chatbot_session_third_step_diagnosis"))
    private ThirdStepDiagnosis thirdStepDiagnosis;
    
    @Column(name = "diagnosis_result", nullable = true)
    private String diagnosisResult;

    // 진단 기반 챗봇 세션 생성자
    public ChatbotSession(ThirdStepDiagnosis thirdStepDiagnosis) {
        if (thirdStepDiagnosis == null) {
            throw new IllegalArgumentException("ThirdStepDiagnosis cannot be null for diagnosis-based session");
        }
        this.sessionType = SessionType.DIAGNOSIS;
        this.thirdStepDiagnosis = thirdStepDiagnosis;
        this.diagnosisResult = thirdStepDiagnosis.getCategory();
    }

    // 일반 챗봇 세션 생성 팩토리 메서드
    public static ChatbotSession createGeneralChatbotSession() {
        ChatbotSession session = new ChatbotSession();
        session.sessionType = SessionType.GENERAL;
        return session;
    }
    
    // 진단 기반 세션인지 확인하는 유틸리티 메서드
    public boolean isDiagnosisBased() {
        return this.sessionType == SessionType.DIAGNOSIS;
    }
    
    // 일반 세션인지 확인하는 유틸리티 메서드
    public boolean isGeneral() {
        return this.sessionType == SessionType.GENERAL;
    }
}
