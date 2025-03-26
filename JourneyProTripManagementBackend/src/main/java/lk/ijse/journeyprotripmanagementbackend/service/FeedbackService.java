package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.FeedbackDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;

import java.util.List;

public interface FeedbackService {
    int submitFeedback(FeedbackDTO feedbackDTO);
    FeedbackDTO getFeedbackById(String feedbackId);
    List<FeedbackDTO> getAllFeedbacksForUser(String userId);
}