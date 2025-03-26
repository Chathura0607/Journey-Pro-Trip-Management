package lk.ijse.journeyprotripmanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private String startPoint;
    private String endPoint;
    private Double distance;
    private String estimatedTime;
}