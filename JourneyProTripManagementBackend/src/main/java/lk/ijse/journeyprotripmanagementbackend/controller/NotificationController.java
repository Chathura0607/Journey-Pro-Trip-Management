package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.NotificationDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.service.NotificationService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllNotificationsForUser(@RequestParam String userId) {
        try {
            List<NotificationDTO> notifications = notificationService.getAllNotificationsForUser(userId);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Notifications fetched successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch notifications", null));
        }
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ResponseDTO> markNotificationAsRead(@PathVariable String notificationId) {
        int result = notificationService.markNotificationAsRead(notificationId);
        if (result == VarList.OK) {
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Notification marked as read", null));
        } else if (result == VarList.Not_Found) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "Notification not found", null));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to mark notification as read", null));
        }
    }
}