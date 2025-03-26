package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.NotificationDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getAllNotificationsForUser(String userId);
    int markNotificationAsRead(String notificationId);
}