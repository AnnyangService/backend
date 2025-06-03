package com.annyang.diagnosis.entity;

import com.annyang.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "third_step_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThirdStepDiagnosis extends BaseEntity {

    @Id
    private String id; // PK이자 FK

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private FirstStepDiagnosis firstStepDiagnosis;

    @Column(nullable = true)
    private String category;

    @Column(nullable = true)
    private double confidence;

    @Builder
    public ThirdStepDiagnosis(String id, String category, double confidence) {
        this.id = id;
        this.category = category;
        this.confidence = confidence;
    }
}
