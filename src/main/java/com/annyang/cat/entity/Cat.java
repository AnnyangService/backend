package com.annyang.cat.entity;

import com.annyang.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Entity
@Data
public class Cat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String name;
    private String image;
    private String birthDate;
    private String breed;
    private String gender;
    private String weight;
    private String lastDiagnosis;
    private String specialNotes;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
} 