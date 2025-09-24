package com.annyang.diagnosis.entity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.annyang.global.entity.BaseEntity;
import com.annyang.member.entity.Member;

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

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @OneToOne(mappedBy = "firstStepDiagnosis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private SecondStepDiagnosis secondStepDiagnosis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_first_step_diagnosis_member"))
    private Member member;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private boolean isNormal;

    @Column(nullable = false)
    private double confidence;

    @Column(nullable = false)
    private String passwordForSecondStep;

    @Builder
    public FirstStepDiagnosis(Member member, String imageUrl, boolean isNormal, double confidence, String passwordForSecondStep) {
        super();
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        this.member = member;
        this.imageUrl = imageUrl;
        this.isNormal = isNormal;
        this.confidence = confidence;
        this.passwordForSecondStep = hashPassword(passwordForSecondStep);
    }

    private String hashPassword(String password) {
        return encoder.encode(password);
    }

    public boolean verifyPassword(String password) {
        return encoder.matches(password, this.passwordForSecondStep);
    }
}
