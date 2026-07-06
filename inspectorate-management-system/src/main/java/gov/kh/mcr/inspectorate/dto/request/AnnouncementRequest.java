package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.Priority;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementRequest {

    @NotBlank(message = "ចំណងជើងមិនអាចទទេបានឡើយ")
    @Size(max = 255, message = "ចំណងជើងមិនអាចលើសពី ២៥៥ តួអក្សរ")
    private String title;

    @NotBlank(message = "ខ្លឹមសារនៃសេចក្តីជូនដំណឹងមិនអាចទទេបានឡើយ")
    private String content;

    @NotNull(message = "កម្រិតអាទិភាពចាំបាច់ត្រូវតែមាន")
    private Priority priority;

    @NotBlank(message = "ស្ថានភាពនៃសេចក្តីជូនដំណឹងចាំបាច់ត្រូវតែមាន")
    private String statusCode;

    @FutureOrPresent(message = "កាលបរិច្ឆេទផ្សព្វផ្សាយមិនអាចជាថ្ងៃអតីតកាលបានទេ")
    private LocalDateTime publishAt;

    @Future(message = "កាលបរិច្ឆេទផុតកំណត់ត្រូវតែជាថ្ងៃអនាគត")
    private LocalDate expireAt;
}