package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendeeRole {

    ORGANIZER  ("ORGANIZER",  "អ្នករៀបចំ"),
    PRESENTER  ("PRESENTER",  "អ្នកបង្ហាញ"),
    ATTENDEE   ("ATTENDEE",   "អ្នកចូលរួម"),
    OBSERVER   ("OBSERVER",   "អ្នកសង្កេត");

    private final String code;
    private final String labelKh;
}