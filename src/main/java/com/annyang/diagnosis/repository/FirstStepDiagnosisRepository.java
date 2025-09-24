package com.annyang.diagnosis.repository;

import java.util.List;

import com.annyang.diagnosis.entity.FirstStepDiagnosis;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FirstStepDiagnosisRepository extends JpaRepository<FirstStepDiagnosis, String> {
    List<FirstStepDiagnosis> findAllByMemberIdOrderByCreatedAtDesc(String memberId);
}
