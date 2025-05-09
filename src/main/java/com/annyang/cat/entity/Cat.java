package com.annyang.cat.entity;

import com.annyang.global.entity.BaseEntity;
import com.annyang.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cat extends BaseEntity {
    
    private String name;
    
    private String image;

    private LocalDate birthDate;

    private String breed;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double weight;

    private LocalDate lastDiagnosis;

    private String specialNotes;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Cat(String name, String image, LocalDate birthDate, String breed, 
              Gender gender, Double weight, LocalDate lastDiagnosis, String specialNotes, Member member) {
        this.name = name;
        this.image = image;
        this.birthDate = birthDate;
        this.breed = breed;
        this.gender = gender;
        this.weight = weight;
        this.lastDiagnosis = lastDiagnosis;
        this.specialNotes = specialNotes;
        this.member = member;
    }

    public void update(String name, String image, LocalDate birthDate, String breed,
                      Gender gender, Double weight, LocalDate lastDiagnosis, String specialNotes) {
        this.name = name;
        this.image = image;
        this.birthDate = birthDate;
        this.breed = breed;
        this.gender = gender;
        this.weight = weight;
        this.lastDiagnosis = lastDiagnosis;
        this.specialNotes = specialNotes;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}