package com.annyang.cat.entity;

import com.annyang.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import lombok.Data;
import java.time.LocalDate;
import com.github.f4b6a3.ulid.UlidCreator;

@Entity
@Data
public class Cat {
    @Id
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

    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UlidCreator.getUlid().toString();
        }
    }
}