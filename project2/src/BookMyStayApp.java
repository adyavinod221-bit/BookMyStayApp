/**
 * UseCase7AddOnServiceSelection.java
 *
 * This class demonstrates Use Case 7 of the Book My Stay App:
 * Add-On Service Selection.
 * Guests can select optional services for their reservations.
 * Services are mapped to reservation IDs, and additional costs are calculated.
 *
 * @author YourName
 * @version 7.1
 */

import java.util.*;

// Reservation class (simplified for demonstration)
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

// Add-On Service class
class Service {
    private String name;
    private double cost;

    public Service(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return name + " (₹" + cost + ")";
    }
}

// Add-On Service Manager
class AddOnServiceManager {
    private Map<String, List<Service>> reservationServices;

    public AddOnServiceManager() {
        reservationServices = new HashMap<>();
    }

    // Attach services to a reservation
    public void addServicesToReservation(Reservation reservation, List<Service> services) {
        reservationServices.put(reservation.getReservationId(), services);
        System.out.println("Services added for Reservation ID: " + reservation.getReservationId());
    }

    // Calculate total additional cost
    public double calculateAdditionalCost(String reservationId) {
        List<Service> services = reservationServices.getOrDefault(reservationId, new ArrayList<>());
        double total = 0;
        for (Service s : services) {
            total += s.getCost();
        }
        return total;
    }

    // Display services for a reservation
    public void displayServices(String reservationId) {
        List<Service> services = reservationServices.getOrDefault(reservationId, new ArrayList<>());
        System.out.println("Add-On Services for Reservation ID: " + reservationId);
        for (Service s : services) {
            System.out.println(" - " + s);
        }
        System.out.println("Total Additional Cost: ₹" + calculateAdditionalCost(reservationId));
    }
}

// Application Entry Point
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App!");
        System.out.println("   Hotel Booking System v7.1");
        System.out.println("=======================================\n");

        // Create reservations
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Suite Room");

        // Define services
        Service breakfast = new Service("Breakfast", 500.0);
        Service spa = new Service("Spa Access", 1500.0);
        Service airportPickup = new Service("Airport Pickup", 1000.0);

        // Initialize service manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Attach services
        manager.addServicesToReservation(r1, Arrays.asList(breakfast, airportPickup));
        manager.addServicesToReservation(r2, Arrays.asList(spa, breakfast));

        // Display services for each reservation
        System.out.println("\nReservation Details:");
        System.out.println(r1);
        manager.displayServices(r1.getReservationId());

        System.out.println("\n" + r2);
        manager.displayServices(r2.getReservationId());
    }
}