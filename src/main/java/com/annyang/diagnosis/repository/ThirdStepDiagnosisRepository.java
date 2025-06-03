package com.annyang.diagnosis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.annyang.diagnosis.entity.ThirdStepDiagnosis;

public interface ThirdStepDiagnosisRepository extends JpaRepository<ThirdStepDiagnosis, String> {
}
