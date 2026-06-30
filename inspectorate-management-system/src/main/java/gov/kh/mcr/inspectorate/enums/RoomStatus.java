package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomStatus {

    AVAILABLE   ("ទំនេរ"),
    OCCUPIED    ("កំពុងប្រើប្រាស់"),
    MAINTENANCE ("កំពុងជួសជុល");

    private final String labelKh;
}