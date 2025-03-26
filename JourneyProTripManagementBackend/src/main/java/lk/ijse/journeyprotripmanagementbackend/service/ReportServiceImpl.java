package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.entity.*;
import lk.ijse.journeyprotripmanagementbackend.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Object generateUserActivityReport() {
        // Fetch all users and their activity (e.g., number of trips, bookings, etc.)
        List<User> users = userRepository.findAll();

        // Create a report map
        Map<String, Object> report = new HashMap<>();
        report.put("totalUsers", users.size());
        report.put("users", users);

        return report;
    }

    @Override
    public Object generateBookingReport() {
        // Fetch all bookings
        List<Booking> bookings = bookingRepository.findAll();

        // Create a report map
        Map<String, Object> report = new HashMap<>();
        report.put("totalBookings", bookings.size());
        report.put("bookings", bookings);

        return report;
    }

    @Override
    public Object generatePaymentReport() {
        // Fetch all payments
        List<Payment> payments = paymentRepository.findAll();

        // Create a report map
        Map<String, Object> report = new HashMap<>();
        report.put("totalPayments", payments.size());
        report.put("payments", payments);

        return report;
    }
}