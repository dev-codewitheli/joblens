package com.codewitheli.joblens.adapter.out.persistence.jpa;

import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Stores a {@link JobAnalysis} as JSON in a single text column. Keeps the schema
 * simple while persisting the full structured result.
 */
@Converter
public class JobAnalysisJsonConverter implements AttributeConverter<JobAnalysis, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JobAnalysis attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize JobAnalysis", e);
        }
    }

    @Override
    public JobAnalysis convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(dbData, JobAnalysis.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize JobAnalysis", e);
        }
    }
}
