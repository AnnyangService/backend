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

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String description;

    @Builder
    public ThirdStepDiagnosis(FirstStepDiagnosis firstStepDiagnosis, String category, String description) {
        this.firstStepDiagnosis = firstStepDiagnosis;
        this.category = category;
        this.description = description;
    }
}
