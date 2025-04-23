package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.BusDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;

import java.util.List;

public interface BusService {
    List<BusDTO> getAllBuses();
    BusDTO getBusById(String busId);
    List<BusDTO> searchBusesByRoute(String route);
    String saveBus(BusDTO busDTO);
    String updateBus(BusDTO busDTO);
    String deleteBus(String busId);
}