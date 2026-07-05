package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.MeetingRoomStatus;
import gov.kh.mcr.inspectorate.service.MeetingRoomService;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomRequest {


    @NotBlank(message = "សូមបញ្ចូលលេខកូដបន្ទប់ប្រជុំ")
    @Size(max = 20, message = "លេខកូដបន្ទប់មិនអាចលើសពី ២០ តួអក្សរឡើយ")
    private String roomCode;

    @NotBlank(message = "សូមបញ្ចូលទីតាំងនៃបន្ទប់ប្រជុំ")
    @Size(max = 255, message = "ទីតាំងមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String location;

    @NotNull(message = "សូមបញ្ចូលចំនួនអ្នកចូលរួមតាមសមត្ថភាពបន្ទប់")
    @Min(value = 1, message = "ចំណុះបន្ទប់ត្រូវមានយ៉ាងតិច ១ នាក់")
    private Integer capacity;

    @NotNull(message = "សូមជ្រើសរើសស្ថានភាពនៃបន្ទប់ប្រជុំ")
    private MeetingRoomStatus status;

    @Size(max = 500, message = "បញ្ជីសម្ភារក្នុងបន្ទប់មិនអាចលើសពី ៥០០ តួអក្សរឡើយ")
    private String facilities;
}