package com.annyang.diagnosis.repository;

import com.annyang.diagnosis.entity.SecondStepDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecondStepDiagnosisRepository extends JpaRepository<SecondStepDiagnosis, String> {
}
