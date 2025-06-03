package com.annyang.diagnosis.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.annyang.global.entity.BaseEntity;

@Entity
@Table(name = "diagnosis_target")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisTarget extends BaseEntity {
    
    @Column(nullable = false, length = 255)
    private String name; // 질병 이름 (ex. 결막염)
    
    @OneToMany(mappedBy = "diagnosisTarget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiagnosisRuleDescription> ruleDescriptions;
    
    public DiagnosisTarget(String name) {
        super();
        this.name = name;
    }
}
