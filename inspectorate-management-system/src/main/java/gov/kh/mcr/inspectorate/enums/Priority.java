package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {

    LOW    ("LOW",    "ទាប"),
    MEDIUM ("MEDIUM", "មធ្យម"),
    HIGH   ("HIGH",   "ខ្ពស់"),
    URGENT ("URGENT", "បន្ទាន់");

    private final String code;
    private final String labelKh;
}