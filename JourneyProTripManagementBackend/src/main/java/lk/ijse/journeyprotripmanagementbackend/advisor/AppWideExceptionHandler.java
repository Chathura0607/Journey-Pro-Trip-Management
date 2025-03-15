package lk.ijse.journeyprotripmanagementbackend.advisor;

import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWideExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> exceptionHandler(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(VarList.Internal_Server_Error, ex.getMessage(), null));
    }
}