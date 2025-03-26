package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.HotelDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Hotel;
import lk.ijse.journeyprotripmanagementbackend.repo.HotelRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<HotelDTO> getAllHotels() {
        // Fetch all hotels from the database
        List<Hotel> hotels = hotelRepository.findAll();

        // Map hotels to HotelDTO
        return hotels.stream()
                .map(hotel -> modelMapper.map(hotel, HotelDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelDTO getHotelById(String hotelId) {
        // Convert the hotelId from String to UUID
        UUID uuid = UUID.fromString(hotelId);

        // Find the hotel by ID
        Optional<Hotel> optionalHotel = hotelRepository.findById(uuid);

        // If the hotel exists, map it to HotelDTO and return
        if (optionalHotel.isPresent()) {
            Hotel hotel = optionalHotel.get();
            return modelMapper.map(hotel, HotelDTO.class);
        } else {
            return null; // Hotel not found
        }
    }

    @Override
    public List<HotelDTO> searchHotelsByLocation(String location) {
        // Fetch hotels by location
        List<Hotel> hotels = hotelRepository.findByLocationContainingIgnoreCase(location);

        // Map hotels to HotelDTO
        return hotels.stream()
                .map(hotel -> modelMapper.map(hotel, HotelDTO.class))
                .collect(Collectors.toList());
    }
}