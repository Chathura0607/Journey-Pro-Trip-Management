package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.TripDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;

import java.util.List;

public interface TripService {
    int createTrip(TripDTO tripDTO);
    TripDTO getTripById(String tripId);
    List<TripDTO> getAllTripsForUser(String userId);
    int updateTrip(String tripId, TripDTO tripDTO);
    int deleteTrip(String tripId);
}