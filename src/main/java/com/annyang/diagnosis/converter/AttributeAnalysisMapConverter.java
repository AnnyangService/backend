package com.annyang.diagnosis.converter;

import com.annyang.diagnosis.entity.ThirdStepDiagnosis.AttributeAnalysis;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Converter
@Slf4j
public class AttributeAnalysisMapConverter implements AttributeConverter<Map<String, AttributeAnalysis>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, AttributeAnalysis> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert AttributeAnalysis map to JSON", e);
            throw new RuntimeException("Failed to convert AttributeAnalysis map to JSON", e);
        }
    }

    @Override
    public Map<String, AttributeAnalysis> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, AttributeAnalysis>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to AttributeAnalysis map", e);
            throw new RuntimeException("Failed to convert JSON to AttributeAnalysis map", e);
        }
    }
}
