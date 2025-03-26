package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.FeedbackDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.service.FeedbackService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/feedbacks")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ResponseDTO> submitFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        int result = feedbackService.submitFeedback(feedbackDTO);
        if (result == VarList.Created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO(VarList.Created, "Feedback submitted successfully", null));
        } else if (result == VarList.Not_Found) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User or Trip not found", null));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to submit feedback", null));
        }
    }

    @GetMapping("/{feedbackId}")
    public ResponseEntity<ResponseDTO> getFeedbackById(@PathVariable String feedbackId) {
        FeedbackDTO feedbackDTO = feedbackService.getFeedbackById(feedbackId);
        if (feedbackDTO != null) {
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Feedback found", feedbackDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "Feedback not found", null));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllFeedbacksForUser(@RequestParam String userId) {
        try {
            List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacksForUser(userId);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Feedbacks fetched successfully", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch feedbacks", null));
        }
    }
}