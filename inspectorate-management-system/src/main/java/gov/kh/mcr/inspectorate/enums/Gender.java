package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    M    ("ប្រុស"),
    F    ("ស្រី"),
    MONK ("បព្វជិត");

    private final String labelKh;
}