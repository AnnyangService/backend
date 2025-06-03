package com.annyang.diagnosis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DiagnosisRuleDescriptionId implements Serializable {
    
    @Column(name = "diagnosis_target_id", length = 255)
    private String diagnosisTargetId;
    
    @Column(name = "diagnosis_rule_id", length = 255)
    private String diagnosisRuleId;
}