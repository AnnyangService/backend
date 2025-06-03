package com.annyang.diagnosis.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.annyang.auth.exception.UnauthorizedException;
import com.annyang.global.entity.BaseEntity;

@Entity
@Table(name = "second_step_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecondStepDiagnosis extends BaseEntity {

    // @MapId 어느테이션을 사용하려면 현재 클래스에서 @Id로 지정된 필드가 있어야해서 BaseEntity에서 @Id로 지정된 필드를 재정의함
    @Id
    private String id; // PK이자 FK

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private FirstStepDiagnosis firstStepDiagnosis;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private double confidence;

    public SecondStepDiagnosis(
        FirstStepDiagnosis firstStepDiagnosis,
        String password,
        String category,
        double confidence
    ) {
        this.firstStepDiagnosis = firstStepDiagnosis;
        if( !firstStepDiagnosis.verifyPassword(password) ) {
            throw new UnauthorizedException();
        }
        this.category = category;
        this.confidence = confidence;
    }
}
