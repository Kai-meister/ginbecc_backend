package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActiveStatus {

    ACTIVE   ("សកម្ម"),
    INACTIVE ("អសកម្ម");

    private final String labelKh;
}