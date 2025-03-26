package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.HotelDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;

import java.util.List;

public interface HotelService {
    List<HotelDTO> getAllHotels();
    HotelDTO getHotelById(String hotelId);
    List<HotelDTO> searchHotelsByLocation(String location);
}