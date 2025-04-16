package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.HotelDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Hotel;
import lk.ijse.journeyprotripmanagementbackend.repo.HotelRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public String saveHotel(HotelDTO hotelDTO) {
        try {
            // Check for duplicate hotel
            if (hotelRepository.existsByNameAndLocation(hotelDTO.getName(), hotelDTO.getLocation())) {
                return String.valueOf(VarList.Duplicate_Entry);
            }

            // Map DTO to entity and save
            Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
            hotelRepository.save(hotel);
            return String.valueOf(VarList.OK);
        } catch (Exception e) {
            return String.valueOf(VarList.Internal_Server_Error);
        }
    }

    @Override
    public String updateHotel(HotelDTO hotelDTO) {
        try {
            // Check if hotel exists
            Optional<Hotel> optionalHotel = hotelRepository.findById(UUID.fromString(hotelDTO.getId()));
            if (optionalHotel.isEmpty()) {
                return String.valueOf(VarList.Not_Found);
            }

            // Update the hotel
            Hotel hotel = optionalHotel.get();
            hotel.setName(hotelDTO.getName());
            hotel.setLocation(hotelDTO.getLocation());
            hotel.setRating(hotelDTO.getRating());
            hotel.setContactInfo(hotelDTO.getContactInfo());

            hotelRepository.save(hotel);
            return String.valueOf(VarList.OK);
        } catch (Exception e) {
            return String.valueOf(VarList.Internal_Server_Error);
        }
    }

    @Override
    public String deleteHotel(String hotelId) {
        try {
            // Check if hotel exists
            Optional<Hotel> optionalHotel = hotelRepository.findById(UUID.fromString(hotelId));
            if (optionalHotel.isEmpty()) {
                return String.valueOf(VarList.Not_Found);
            }

            // Delete the hotel
            hotelRepository.deleteById(UUID.fromString(hotelId));
            return String.valueOf(VarList.OK);
        } catch (Exception e) {
            return String.valueOf(VarList.Internal_Server_Error);
        }
    }

    @Override
    public List<HotelDTO> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(hotel -> {
                    HotelDTO dto = modelMapper.map(hotel, HotelDTO.class);
                    dto.setId(hotel.getId().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public HotelDTO getHotelById(String hotelId) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(UUID.fromString(hotelId));
        if (optionalHotel.isPresent()) {
            HotelDTO dto = modelMapper.map(optionalHotel.get(), HotelDTO.class);
            dto.setId(optionalHotel.get().getId().toString());
            return dto;
        }
        return null;
    }

    @Override
    public List<HotelDTO> searchHotelsByLocation(String location) {
        List<Hotel> hotels = hotelRepository.findByLocationContainingIgnoreCase(location);
        return hotels.stream()
                .map(hotel -> {
                    HotelDTO dto = modelMapper.map(hotel, HotelDTO.class);
                    dto.setId(hotel.getId().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}