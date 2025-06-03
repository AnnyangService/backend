package com.annyang.diagnosis.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diagnosis_rule_description")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class DiagnosisRuleDescription {
    
    @EmbeddedId
    private DiagnosisRuleDescriptionId id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diagnosisTargetId")
    @JoinColumn(name = "diagnosis_target_id", nullable = false)
    private DiagnosisTarget diagnosisTarget;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diagnosisRuleId")
    @JoinColumn(name = "diagnosis_rule_id", nullable = false)
    private DiagnosisRule diagnosisRule;
    
    @Column(nullable = false, length = 255)
    private String description; // ex. 눈을 뜨는데 어려움이 없고 어쩌고 룰
    
    @Builder
    public DiagnosisRuleDescription(DiagnosisTarget diagnosisTarget, 
                                   DiagnosisRule diagnosisRule, 
                                   String description) {
        this.id = new DiagnosisRuleDescriptionId(diagnosisTarget.getId(), diagnosisRule.getId());
        this.diagnosisTarget = diagnosisTarget;
        this.diagnosisRule = diagnosisRule;
        this.description = description;
    }
}