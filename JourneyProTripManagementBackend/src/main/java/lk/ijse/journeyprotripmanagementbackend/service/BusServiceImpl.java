package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.BusDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Bus;
import lk.ijse.journeyprotripmanagementbackend.repo.BusRepository;
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
public class BusServiceImpl implements BusService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<BusDTO> getAllBuses() {
        List<Bus> buses = busRepository.findAll();
        return buses.stream()
                .map(bus -> modelMapper.map(bus, BusDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BusDTO getBusById(String busId) {
        try {
            UUID uuid = UUID.fromString(busId);
            Optional<Bus> optionalBus = busRepository.findById(uuid);
            return optionalBus.map(bus -> modelMapper.map(bus, BusDTO.class)).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<BusDTO> searchBusesByRoute(String route) {
        List<Bus> buses = busRepository.findByRouteContainingIgnoreCase(route);
        return buses.stream()
                .map(bus -> modelMapper.map(bus, BusDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String saveBus(BusDTO busDTO) {
        // Check if bus with this number already exists
        if (busRepository.existsByBusNumber(busDTO.getBusNumber())) {
            return String.valueOf(VarList.Not_Acceptable);
        }

        Bus bus = modelMapper.map(busDTO, Bus.class);
        busRepository.save(bus);
        return String.valueOf(VarList.OK);
    }

    @Override
    public String updateBus(BusDTO busDTO) {
        try {
            UUID uuid = UUID.fromString(busDTO.getId());
            if (busRepository.existsById(uuid)) {
                Bus existingBus = busRepository.findById(uuid).orElse(null);
                if (existingBus != null) {
                    // Check if bus number is being changed to one that already exists
                    if (!existingBus.getBusNumber().equals(busDTO.getBusNumber())) {
                        if (busRepository.existsByBusNumber(busDTO.getBusNumber())) {
                            return String.valueOf(VarList.Not_Acceptable);
                        }
                    }

                    modelMapper.map(busDTO, existingBus);
                    busRepository.save(existingBus);
                    return String.valueOf(VarList.OK);
                }
            }
            return String.valueOf(VarList.Not_Found);
        } catch (IllegalArgumentException e) {
            return String.valueOf(VarList.Bad_Request);
        }
    }

    @Override
    public String deleteBus(String busId) {
        try {
            UUID uuid = UUID.fromString(busId);
            if (busRepository.existsById(uuid)) {
                busRepository.deleteById(uuid);
                return String.valueOf(VarList.OK);
            } else {
                return String.valueOf(VarList.Not_Found);
            }
        } catch (IllegalArgumentException e) {
            return String.valueOf(VarList.Bad_Request);
        }
    }
}