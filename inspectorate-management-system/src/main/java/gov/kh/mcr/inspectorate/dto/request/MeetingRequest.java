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

    @NotBlank(message = "ចំណងជើងចាំបាច់")
    @Size(max = 255)
    private String title;

    private String description;

    @NotNull(message = "ប្រភេទប្រជុំចាំបាច់")
    private MeetingType meetingType;

    @NotNull(message = "ថ្ងៃប្រជុំចាំបាច់")
    @FutureOrPresent(
            message = "ថ្ងៃប្រជុំ"
                    + "ត្រូវជាថ្ងៃនេះ ឬ ថ្ងៃអនាគត")
    private LocalDate meetingDate;

    @NotNull(message = "ម៉ោងចាប់ផ្ដើមចាំបាច់")
    private LocalTime startTime;

    @NotNull(message = "ម៉ោងបញ្ចប់ចាំបាច់")
    private LocalTime endTime;

    // nullable — online meeting
    private Integer roomId;

    // nullable — physical meeting
    private String meetingLink;

    @NotBlank(
            message = "ស្ថានភាពចាំបាច់")
    private String statusCode;
}