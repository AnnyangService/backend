package com.annyang.diagnosis.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.annyang.global.entity.BaseEntity;

@Entity
@Table(name = "diagnosis_rule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisRule extends BaseEntity {
    
    @Column(nullable = false, length = 255)
    private String name; // ex. 증상, 증상 진행 속도
    
    @OneToMany(mappedBy = "diagnosisRule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiagnosisRuleDescription> ruleDescriptions;
    
    @Builder
    public DiagnosisRule(String name) {
        super();
        this.name = name;
    }
}