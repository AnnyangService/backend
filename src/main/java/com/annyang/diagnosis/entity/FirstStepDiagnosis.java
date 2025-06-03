package com.annyang.diagnosis.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "first_step_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FirstStepDiagnosis {

    @Id
    @Column(length = 30)
    private String id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean isNormal;

    @Column(nullable = false)
    private double confidence;

    @OneToOne(mappedBy = "firstStepDiagnosis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SecondStepDiagnosis secondStepDiagnosis;

    @Builder
    public FirstStepDiagnosis(String id, String imageUrl, boolean isNormal, double confidence) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.isNormal = isNormal;
        this.confidence = confidence;
    }
}
