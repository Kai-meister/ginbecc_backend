package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.MeetingType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingRequest {

    private Integer roomId;

    @NotBlank(message = "ចំណងជើងចាំបាច់")
    @Size(max = 255,
            message = "ចំណងជើងអតិបរមា 255 តួ")
    private String title;

    @NotNull(message = "ថ្ងៃប្រជុំចាំបាច់")
    @FutureOrPresent(
            message = "ថ្ងៃប្រជុំមិនអាចជាអតីតកាល")
    private LocalDate meetingDate;

    @NotNull(message = "ម៉ោងចាប់ផ្តើមចាំបាច់")
    private LocalTime startTime;

    @NotNull(message = "ម៉ោងបញ្ចប់ចាំបាច់")
    private LocalTime endTime;

    @NotNull(message = "ប្រភេទចាំបាច់")
    private MeetingType meetingType;

    @Size(max = 500,
            message = "Link អតិបរមា 500 តួ")
    private String meetingLink;

    @NotBlank(message = "ស្ថានភាពចាំបាច់")
    private String statusCode;

    private String agenda;
    private String note;
}