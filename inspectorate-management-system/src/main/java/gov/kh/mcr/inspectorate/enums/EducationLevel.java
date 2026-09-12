package gov.kh.mcr.inspectorate.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum EducationLevel {

    BACHELOR("បរិញ្ញាបត្រ"),
    MASTER("អនុបណ្ឌិត"),
    DOCTORATE("បណ្ឌិត");

    private final String labelKh;

    EducationLevel(String labelKh) {
        this.labelKh = labelKh;
    }

    @JsonValue
    public String getLabelKh() {
        return labelKh;
    }

    /**
     * JSON / API parsing — rejects unknown values.
     */
    @JsonCreator
    public static EducationLevel fromValue(String value) {
        EducationLevel level = fromStoredValue(value);
        if (level == null && value != null && !value.isBlank()) {
            throw new IllegalArgumentException(
                    "កម្រិតអប់រំ មិនត្រឹមត្រូវ: " + value);
        }
        return level;
    }

    /**
     * JPA / DB parsing — tolerates blank and maps legacy English labels.
     */
    public static EducationLevel fromStoredValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        for (EducationLevel level : values()) {
            if (level.name().equalsIgnoreCase(trimmed)
                    || level.labelKh.equals(trimmed)) {
                return level;
            }
        }

        String normalized = trimmed
                .toLowerCase(Locale.ROOT)
                .replace('’', '\'')
                .replace(".", "")
                .replace("'", "")
                .replace("-", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return switch (normalized) {
            case "bachelors degree",
                 "bachelor degree",
                 "bachelor",
                 "undergraduate" -> BACHELOR;
            case "masters degree",
                 "master degree",
                 "master",
                 "msc",
                 "ma" -> MASTER;
            case "doctorate",
                 "doctoral",
                 "phd",
                 "ph d",
                 "doctor" -> DOCTORATE;
            default -> null;
        };
    }
}
