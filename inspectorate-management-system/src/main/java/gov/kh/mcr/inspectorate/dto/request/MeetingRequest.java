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
    @NotBlank(message = "សូមបញ្ចូលកម្មវត្ថុ ឬចំណងជើងនៃកិច្ចប្រជុំ")
    @Size(max = 255, message = "ចំណងជើងនៃកិច្ចប្រជុំមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String title;

    private String description;

    @NotNull(message = "សូមជ្រើសរើសប្រភេទកិច្ចប្រជុំ")
    private MeetingType meetingType;

    @NotNull(message = "សូមជ្រើសរើសកាលបរិច្ឆេទប្រជុំ")
    @FutureOrPresent(message = "កាលបរិច្ឆេទប្រជុំត្រូវតែជាថ្ងៃនេះ ឬថ្ងៃក្នុងពេលអនាគត")
    private LocalDate meetingDate;

    @NotNull(message = "សូមជ្រើសរើសម៉ោងចាប់ផ្តើម")
    private LocalTime startTime;

    @NotNull(message = "សូមជ្រើសរើសម៉ោងបញ្ចប់")
    private LocalTime endTime;

    // nullable — online meeting
    private Integer roomId;
    // nullable — physical meeting
    private String meetingLink;

    @NotBlank(message = "សូមជ្រើសរើសស្ថានភាព")
    private String statusCode;
}