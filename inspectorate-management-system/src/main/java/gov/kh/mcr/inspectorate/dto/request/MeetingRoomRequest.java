package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.RoomStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomRequest {

    @NotBlank(message = "សូមបញ្ចូលលេខកូដបន្ទប់")
    @Size(max = 20, message = "លេខកូដបន្ទប់មិនអាចលើសពី ២០ តួអក្សរឡើយ")
    private String roomCode;

    @Size(max = 255, message = "ទីតាំងមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String location;

    @Min(value = 1, message = "ចំណុះ ឬសមត្ថភាពផ្ទុកអប្បបរមាត្រូវចាប់ពី ១ នាក់ឡើងទៅ")
    private Integer capacity;

    @NotNull(message = "សូមជ្រើសរើសស្ថានភាពបន្ទប់")
    private RoomStatus status;

    private String  facilities;
}