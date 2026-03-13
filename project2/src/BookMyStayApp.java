/**
 * UseCase8BookingHistoryReport.java
 *
 * This class demonstrates Use Case 8 of the Book My Stay App:
 * Booking History & Reporting.
 * Confirmed reservations are stored in chronological order,
 * and reports can be generated for administrative review.
 *
 * @author YourName
 * @version 8.1
 */

import java.util.*;

// Reservation class (simplified for reporting)
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.reservationId = UUID.randomUUID().toString();
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation [ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType + "]";
    }
}

// BookingHistory stores confirmed reservations
class BookingHistory {
    private List<Reservation> history;

    public BookingHistory() {
        history = new ArrayList<>();
    }

    // Add confirmed reservation
    public void addReservation(Reservation reservation) {
        history.add(reservation);
        System.out.println("Reservation confirmed and added to history: " + reservation);
    }

    // Retrieve all reservations
    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(history);
    }
}

// BookingReportService generates reports
class BookingReportService {
    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    // Display all reservations
    public void displayBookingHistory() {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : history.getAllReservations()) {
            System.out.println(r);
        }
        System.out.println("========================");
    }

    // Generate summary report by room type
    public void generateSummaryReport() {
        System.out.println("\n=== Booking Summary Report ===");
        Map<String, Integer> summary = new HashMap<>();
        for (Reservation r : history.getAllReservations()) {
            summary.put(r.getRoomType(), summary.getOrDefault(r.getRoomType(), 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : summary.entrySet()) {
            System.out.println("Room Type: " + entry.getKey() +
                    " | Total Bookings: " + entry.getValue());
        }
        System.out.println("===============================");
    }
}

// Application Entry Point
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App!");
        System.out.println("   Hotel Booking System v8.1");
        System.out.println("=======================================\n");

        // Initialize booking history
        BookingHistory history = new BookingHistory();

        // Simulate confirmed reservations
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Suite Room");
        Reservation r3 = new Reservation("Charlie", "Single Room");
        Reservation r4 = new Reservation("Diana", "Double Room");

        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);
        history.addReservation(r4);

        // Initialize report service
        BookingReportService reportService = new BookingReportService(history);

        // Display booking history
        reportService.displayBookingHistory();

        // Generate summary report
        reportService.generateSummaryReport();
    }
}