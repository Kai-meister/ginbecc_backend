package gov.kh.mcr.inspectorate.converter;

import gov.kh.mcr.inspectorate.enums.EducationLevel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persists {@link EducationLevel#name()} while accepting legacy English labels
 * that were written before the enum names were standardized
 * (e.g. {@code Bachelor's Degree}).
 */
@Converter(autoApply = false)
public class EducationLevelConverter
        implements AttributeConverter<EducationLevel, String> {

    @Override
    public String convertToDatabaseColumn(
            EducationLevel attribute) {
        return attribute == null
                ? null
                : attribute.name();
    }

    @Override
    public EducationLevel convertToEntityAttribute(
            String dbData) {
        return EducationLevel.fromStoredValue(dbData);
    }
}
