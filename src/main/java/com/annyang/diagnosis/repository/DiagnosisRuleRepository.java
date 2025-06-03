package com.annyang.diagnosis.repository;

import com.annyang.diagnosis.entity.DiagnosisRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRuleRepository extends JpaRepository<DiagnosisRule, String> {
    List<DiagnosisRule> findAll();
}