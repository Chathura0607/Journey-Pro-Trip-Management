package lk.ijse.journeyprotripmanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component // Add this annotation
public class ResponseDTO {
    private int code;
    private String message;
    private Object data;
}