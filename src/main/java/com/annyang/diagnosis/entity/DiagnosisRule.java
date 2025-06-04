package com.annyang.diagnosis.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "diagnosis_rule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisRule {

    @Id
    private Integer id; // PK이자 FK, BaseEntity에서 상속받음
    
    @Column(nullable = false, length = 255)
    private String name; // ex. 증상, 증상 진행 속도
    
    @OneToMany(mappedBy = "diagnosisRule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiagnosisRuleDescription> ruleDescriptions;
    
    @Builder
    public DiagnosisRule(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}