package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingType {

    INTERNAL ("ប្រជុំផ្ទៃក្នុង"),
    EXTERNAL ("ប្រជុំក្រៅ"),
    ONLINE   ("ប្រជុំអនឡាញ");

    private final String labelKh;
}