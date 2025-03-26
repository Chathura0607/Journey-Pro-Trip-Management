package lk.ijse.journeyprotripmanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private String userId; // ID of the user submitting the feedback
    private String tripId; // ID of the trip
    private String comment;
    private Integer rating; // 1-5 scale
}