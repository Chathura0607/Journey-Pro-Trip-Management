package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.RouteDTO;
import lk.ijse.journeyprotripmanagementbackend.service.RouteService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@CrossOrigin(origins = "*")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllRoutes() {
        try {
            List<RouteDTO> routes = routeService.getAllRoutes();
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Routes fetched successfully", routes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch routes", null));
        }
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<ResponseDTO> getRouteById(@PathVariable String routeId) {
        try {
            RouteDTO routeDTO = routeService.getRouteById(routeId);
            if (routeDTO != null) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Route found", routeDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Route not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch route", null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> addNewRoute(@RequestBody RouteDTO routeDTO) {
        try {
            int result = routeService.addNewRoute(routeDTO);
            if (result == VarList.Created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Route added successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to add route", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to add route", null));
        }
    }
}