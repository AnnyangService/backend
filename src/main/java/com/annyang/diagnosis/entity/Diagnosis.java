package com.annyang.diagnosis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diagnosis {

    @Id
    @Column(length = 30)
    private String id;

    @Column(nullable = false)
    private String imageUrl;

    private String password; // 해싱된 비밀번호 저장

    private boolean normal; // 1차 진단 결과

    private double confidence; // 1차 진단 신뢰도
    
    @Column(nullable = true)
    private double confidenceOfSecond; // 2차 진단 신뢰도

    private String category; // 2차 진단 결과 카테고리

    @Builder
    public Diagnosis(String id, String imageUrl, boolean normal, double confidence, String password, String category, double confidenceOfSecond) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.normal = normal;
        this.confidence = confidence;
        this.password = password;
        this.category = category;
        this.confidenceOfSecond = confidenceOfSecond;
    }

    public void updateSecondDiagnosis(String category, double confidenceOfSecond) {
        this.category = category;
        this.confidenceOfSecond = confidenceOfSecond;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
