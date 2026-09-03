package gov.kh.mcr.inspectorate.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

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

    @JsonCreator
    public static EducationLevel fromValue(String value) {
        for (EducationLevel level : values()) {
            if (level.labelKh.equals(value)) {
                return level;
            }
        }

        throw new IllegalArgumentException(
                "កម្រិតអប់រំ មិនត្រឹមត្រូវ: " + value
        );
    }
}
