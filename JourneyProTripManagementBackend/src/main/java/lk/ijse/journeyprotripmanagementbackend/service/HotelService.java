package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.HotelDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;

import java.util.List;

public interface HotelService {
    String saveHotel(HotelDTO hotelDTO);
    String updateHotel(HotelDTO hotelDTO);
    String deleteHotel(String hotelId);
    List<HotelDTO> getAllHotels();
    HotelDTO getHotelById(String hotelId);
    List<HotelDTO> searchHotelsByLocation(String location);
}