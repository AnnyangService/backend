package com.annyang.cat.entity;

import com.annyang.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import java.time.LocalDate;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Data
public class Cat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
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
}