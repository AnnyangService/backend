package com.annyang.diagnosis.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.annyang.auth.exception.UnauthorizedException;

@Entity
@Table(name = "second_step_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecondStepDiagnosis {

    @Id
    @Column(name = "id", length = 30)
    private String id; // PK이자 FK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private FirstStepDiagnosis firstStepDiagnosis;

    @Column(nullable = true)
    private String category;

    @Column(nullable = true)
    private double confidence;

    @Column(nullable = false)
    private String password;

    @Builder
    public SecondStepDiagnosis(String id, String password) {
        this.id = id;
        this.password = hashPassword(password);
    }

    public void updateDiagnosis(String password, String category, double confidence) {
        if (!verifyPassword(password)) {
            throw new UnauthorizedException();
        }
        this.category = category;
        this.confidence = confidence;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    private String hashPassword(String password) {
        return java.util.Base64.getEncoder().encodeToString(password.getBytes());
    }

    private boolean verifyPassword(String password) {
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(this.password);
    }
}
