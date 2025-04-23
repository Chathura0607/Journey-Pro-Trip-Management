package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.TripDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Admin;
import lk.ijse.journeyprotripmanagementbackend.entity.Trip;
import lk.ijse.journeyprotripmanagementbackend.entity.User;
import lk.ijse.journeyprotripmanagementbackend.enums.TripStatus;
import lk.ijse.journeyprotripmanagementbackend.repo.AdminRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.TripRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.UserRepository;
import lk.ijse.journeyprotripmanagementbackend.service.AdminTripService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminTripServiceImpl implements AdminTripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TripDTO> getAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        return trips.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> getTripsByStatus(String status) {
        try {
            TripStatus tripStatus = TripStatus.valueOf(status.toUpperCase());
            List<Trip> trips = tripRepository.findByStatus(tripStatus);
            return trips.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<TripDTO> getTripsByUser(String userId) {
        try {
            UUID uuid = UUID.fromString(userId);
            List<Trip> trips = tripRepository.findByUserId(uuid);
            return trips.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<TripDTO> getTripsByAdmin(String adminId) {
        try {
            UUID uuid = UUID.fromString(adminId);
            List<Trip> trips = tripRepository.findByAdminId(uuid);
            return trips.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public TripDTO getTripById(String tripId) {
        try {
            UUID uuid = UUID.fromString(tripId);
            Optional<Trip> trip = tripRepository.findById(uuid);
            return trip.map(this::convertToDto).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public int createTrip(TripDTO tripDTO) {
        try {
            UUID userId = UUID.fromString(tripDTO.getUserId());
            Optional<User> user = userRepository.findById(userId);
            Optional<Admin> admin = tripDTO.getAdminId() != null ?
                    adminRepository.findById(tripDTO.getAdminId()) :
                    Optional.empty();

            if (user.isPresent()) {
                Trip trip = new Trip();
                trip.setUser(user.get());
                admin.ifPresent(trip::setAdmin);
                trip.setDestination(tripDTO.getDestination());
                trip.setStartDate(tripDTO.getStartDate());
                trip.setEndDate(tripDTO.getEndDate());
                trip.setStatus(tripDTO.getStatus() != null ?
                        tripDTO.getStatus() : TripStatus.UPCOMING);
                trip.setCreatedAt(LocalDateTime.now());

                tripRepository.save(trip);
                return VarList.Created;
            }
            return VarList.Not_Found;
        } catch (Exception e) {
            return VarList.Internal_Server_Error;
        }
    }

    @Override
    public int updateTrip(String tripId, TripDTO tripDTO) {
        try {
            UUID uuid = UUID.fromString(tripId);
            Optional<Trip> optionalTrip = tripRepository.findById(uuid);

            if (optionalTrip.isPresent()) {
                Trip trip = optionalTrip.get();
                trip.setDestination(tripDTO.getDestination());
                trip.setStartDate(tripDTO.getStartDate());
                trip.setEndDate(tripDTO.getEndDate());

                if (tripDTO.getStatus() != null) {
                    trip.setStatus(tripDTO.getStatus());
                }

                tripRepository.save(trip);
                return VarList.OK;
            }
            return VarList.Not_Found;
        } catch (Exception e) {
            return VarList.Internal_Server_Error;
        }
    }

    @Override
    public int updateTripStatus(String tripId, String status) {
        try {
            UUID uuid = UUID.fromString(tripId);
            TripStatus tripStatus = TripStatus.valueOf(status.toUpperCase());
            Optional<Trip> optionalTrip = tripRepository.findById(uuid);

            if (optionalTrip.isPresent()) {
                Trip trip = optionalTrip.get();
                trip.setStatus(tripStatus);
                tripRepository.save(trip);
                return VarList.OK;
            }
            return VarList.Not_Found;
        } catch (IllegalArgumentException e) {
            return VarList.Bad_Request;
        } catch (Exception e) {
            return VarList.Internal_Server_Error;
        }
    }

    @Override
    public int deleteTrip(String tripId) {
        try {
            UUID uuid = UUID.fromString(tripId);
            if (tripRepository.existsById(uuid)) {
                tripRepository.deleteById(uuid);
                return VarList.OK;
            }
            return VarList.Not_Found;
        } catch (Exception e) {
            return VarList.Internal_Server_Error;
        }
    }

    private TripDTO convertToDto(Trip trip) {
        TripDTO tripDTO = modelMapper.map(trip, TripDTO.class);
        tripDTO.setUserId(trip.getUser().getId().toString());
        if (trip.getAdmin() != null) {
            tripDTO.setAdminId(trip.getAdmin().getId().toString());
        }
        return tripDTO;
    }
}