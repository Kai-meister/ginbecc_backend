package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceStatus {

    INVITED   ("INVITED",   "បានអញ្ជើញ"),
    CONFIRMED ("CONFIRMED", "បានបញ្ជាក់"),
    ATTENDED  ("ATTENDED",  "បានចូលរួម"),
    ABSENT    ("ABSENT",    "អវត្តមាន");

    private final String code;
    private final String labelKh;
}