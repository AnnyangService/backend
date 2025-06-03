package com.annyang.diagnosis.entity;

import com.annyang.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "first_step_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FirstStepDiagnosis extends BaseEntity {

    @OneToOne(mappedBy = "firstStepDiagnosis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private SecondStepDiagnosis secondStepDiagnosis;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean isNormal;

    @Column(nullable = false)
    private double confidence;

    @Builder
    public FirstStepDiagnosis(String imageUrl, boolean isNormal, double confidence) {
        super();
        this.imageUrl = imageUrl;
        this.isNormal = isNormal;
        this.confidence = confidence;
    }
}
