package com.annyang.cat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatRegisterRequest {
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    
    private String image;
    
    @NotBlank(message = "생년월일은 필수입니다")
    private String birthDate;
    
    @NotBlank(message = "품종은 필수입니다")
    private String breed;
    
    @NotBlank(message = "성별은 필수입니다")
    private String gender;
    
    private String weight;
    private String lastDiagnosis;
    private String specialNotes;
} 