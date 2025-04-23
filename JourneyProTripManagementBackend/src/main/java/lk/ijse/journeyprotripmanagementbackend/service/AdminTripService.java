package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.TripDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;

import java.util.List;

public interface AdminTripService {
    List<TripDTO> getAllTrips();
    List<TripDTO> getTripsByStatus(String status);
    List<TripDTO> getTripsByUser(String userId);
    List<TripDTO> getTripsByAdmin(String adminId);
    TripDTO getTripById(String tripId);
    int createTrip(TripDTO tripDTO);
    int updateTrip(String tripId, TripDTO tripDTO);
    int updateTripStatus(String tripId, String status);
    int deleteTrip(String tripId);
}