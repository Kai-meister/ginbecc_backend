package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {

    OFFICER          ("មន្ត្រីរាជការ"),
    CONTRACT_OFFICER ("មន្ត្រីកិច្ចសន្យា");

    private final String labelKh;
}