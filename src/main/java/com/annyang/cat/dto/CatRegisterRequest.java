package com.annyang.cat.dto;

import com.annyang.cat.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class CatRegisterRequest {
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    
    private String image;
    
    @NotNull(message = "생년월일은 필수입니다")
    private LocalDate birthDate;
    
    @NotBlank(message = "품종은 필수입니다")
    private String breed;
    
    @NotNull(message = "성별은 필수입니다")
    private Gender gender;
    
    private Double weight;
    private LocalDate lastDiagnosis;
    private String specialNotes;
} 