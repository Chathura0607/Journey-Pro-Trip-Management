package lk.ijse.journeyprotripmanagementbackend.service;

public interface ReportService {
    Object generateUserActivityReport();
    Object generateBookingReport();
    Object generatePaymentReport();
}