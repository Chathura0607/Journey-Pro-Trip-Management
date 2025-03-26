package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.NotificationDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Notification;
import lk.ijse.journeyprotripmanagementbackend.entity.User;
import lk.ijse.journeyprotripmanagementbackend.repo.NotificationRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.UserRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<NotificationDTO> getAllNotificationsForUser(String userId) {
        // Convert the userId from String to UUID
        UUID uuid = UUID.fromString(userId);

        // Find all notifications for the user
        List<Notification> notifications = notificationRepository.findByUserId(uuid);

        // Map notifications to NotificationDTO
        return notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public int markNotificationAsRead(String notificationId) {
        // Convert the notificationId from String to UUID
        UUID uuid = UUID.fromString(notificationId);

        // Find the notification by ID
        Optional<Notification> optionalNotification = notificationRepository.findById(uuid);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setIsRead(true); // Mark as read
            notificationRepository.save(notification);
            return VarList.OK; // Success
        } else {
            return VarList.Not_Found; // Notification not found
        }
    }
}