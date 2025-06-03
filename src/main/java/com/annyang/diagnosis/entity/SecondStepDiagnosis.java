package com.annyang.diagnosis.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.annyang.auth.exception.UnauthorizedException;

@Entity
@Table(name = "second_step_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class SecondStepDiagnosis {

    @Id
    @Column(name = "id", length = 30)
    private String id; // PK이자 FK

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private FirstStepDiagnosis firstStepDiagnosis;

    @Column(nullable = true)
    private String category;

    @Column(nullable = true)
    private double confidence;

    @Column(nullable = false)
    private String password;

    public SecondStepDiagnosis(FirstStepDiagnosis firstStepDiagnosis, String password) {
        this.firstStepDiagnosis = firstStepDiagnosis;
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
