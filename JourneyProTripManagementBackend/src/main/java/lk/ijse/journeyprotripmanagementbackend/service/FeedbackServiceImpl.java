package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.FeedbackDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Feedback;
import lk.ijse.journeyprotripmanagementbackend.entity.Trip;
import lk.ijse.journeyprotripmanagementbackend.entity.User;
import lk.ijse.journeyprotripmanagementbackend.repo.FeedbackRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.TripRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.UserRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int submitFeedback(FeedbackDTO feedbackDTO) {
        // Convert the userId and tripId from String to UUID
        UUID userId = UUID.fromString(feedbackDTO.getUserId());
        UUID tripId = UUID.fromString(feedbackDTO.getTripId());

        // Find the user and trip by ID
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);

        if (optionalUser.isPresent() && optionalTrip.isPresent()) {
            User user = optionalUser.get();
            Trip trip = optionalTrip.get();

            // Create a new feedback
            Feedback feedback = new Feedback();
            feedback.setUser(user);
            feedback.setTrip(trip);
            feedback.setComment(feedbackDTO.getComment());
            feedback.setRating(feedbackDTO.getRating());
            feedback.setCreatedAt(LocalDateTime.now());

            // Save the feedback
            feedbackRepository.save(feedback);
            return VarList.Created; // Success
        } else {
            return VarList.Not_Found; // User or Trip not found
        }
    }

    @Override
    public FeedbackDTO getFeedbackById(String feedbackId) {
        // Convert the feedbackId from String to UUID
        UUID uuid = UUID.fromString(feedbackId);

        // Find the feedback by ID
        Optional<Feedback> optionalFeedback = feedbackRepository.findById(uuid);

        // If the feedback exists, map it to FeedbackDTO and return
        if (optionalFeedback.isPresent()) {
            Feedback feedback = optionalFeedback.get();
            return modelMapper.map(feedback, FeedbackDTO.class);
        } else {
            return null; // Feedback not found
        }
    }

    @Override
    public List<FeedbackDTO> getAllFeedbacksForUser(String userId) {
        // Convert the userId from String to UUID
        UUID uuid = UUID.fromString(userId);

        // Find all feedbacks for the user
        List<Feedback> feedbacks = feedbackRepository.findByUserId(uuid);

        // Map feedbacks to FeedbackDTO
        return feedbacks.stream()
                .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
                .collect(Collectors.toList());
    }
}