package com.annyang.chatbot.entity;

import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.annyang.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "third_step_diagnosis_id", foreignKey = @ForeignKey(name = "fk_chatbot_session_third_step_diagnosis"))
    private ThirdStepDiagnosis thirdStepDiagnosis;
    
    @Column(name = "diagnosis_result")
    private String diagnosisResult;

    public ChatbotSession(ThirdStepDiagnosis thirdStepDiagnosis) {
        this.thirdStepDiagnosis = thirdStepDiagnosis;
        this.diagnosisResult = thirdStepDiagnosis.getCategory();
    }
}
