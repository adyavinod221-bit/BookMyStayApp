/**
 * UseCase10BookingCancellation.java
 *
 * This class demonstrates Use Case 10 of the Book My Stay App:
 * Booking Cancellation & Inventory Rollback.
 * Guests can cancel confirmed reservations, inventory is restored,
 * and room IDs are tracked using a rollback stack.
 *
 * @author YourName
 * @version 10.1
 */

import java.util.*;

// Reservation class
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType, String roomId) {
        this.reservationId = UUID.randomUUID().toString();
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }

    @Override
    public String toString() {
        return "Reservation [ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType +
                ", Room ID: " + roomId + "]";
    }
}

// RoomInventory manages availability
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String roomType, int availability) {
        inventory.put(roomType, availability);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void incrementAvailability(String roomType) {
        inventory.put(roomType, getAvailability(roomType) + 1);
    }

    public void decrementAvailability(String roomType) {
        int current = getAvailability(roomType);
        if (current > 0) inventory.put(roomType, current - 1);
    }

    public void displayInventory() {
        System.out.println("\n=== Current Room Inventory ===");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println("Room Type: " + entry.getKey() +
                    " | Availability: " + entry.getValue());
        }
        System.out.println("===============================");
    }
}

// BookingHistory stores confirmed reservations
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        history.add(reservation);
        System.out.println("Reservation confirmed: " + reservation);
    }

    public boolean removeReservation(String reservationId) {
        return history.removeIf(r -> r.getReservationId().equals(reservationId));
    }

    public Reservation findReservation(String reservationId) {
        for (Reservation r : history) {
            if (r.getReservationId().equals(reservationId)) return r;
        }
        return null;
    }

    public void displayHistory() {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : history) System.out.println(r);
        System.out.println("========================");
    }
}

// CancellationService handles rollback
class CancellationService {
    private RoomInventory inventory;
    private BookingHistory history;
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void cancelReservation(String reservationId) {
        Reservation reservation = history.findReservation(reservationId);

        if (reservation == null) {
            System.out.println("Cancellation failed: Reservation not found.");
            return;
        }

        // Controlled rollback
        rollbackStack.push(reservation.getRoomId());
        inventory.incrementAvailability(reservation.getRoomType());
        history.removeReservation(reservationId);

        System.out.println("Cancellation successful for Guest: " + reservation.getGuestName() +
                " | Room Type: " + reservation.getRoomType() +
                " | Released Room ID: " + reservation.getRoomId());
    }

    public void displayRollbackStack() {
        System.out.println("\n=== Rollback Stack (Released Room IDs) ===");
        for (String roomId : rollbackStack) {
            System.out.println("Released Room ID: " + roomId);
        }
        System.out.println("==========================================");
    }
}

// Application Entry Point
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App!");
        System.out.println("   Hotel Booking System v10.1");
        System.out.println("=======================================\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 1);
        inventory.addRoomType("Double Room", 1);

        // Initialize booking history
        BookingHistory history = new BookingHistory();

        // Simulate confirmed reservations
        Reservation r1 = new Reservation("Alice", "Single Room", UUID.randomUUID().toString());
        Reservation r2 = new Reservation("Bob", "Double Room", UUID.randomUUID().toString());

        history.addReservation(r1);
        inventory.decrementAvailability(r1.getRoomType());

        history.addReservation(r2);
        inventory.decrementAvailability(r2.getRoomType());

        history.displayHistory();
        inventory.displayInventory();

        // Initialize cancellation service
        CancellationService cancellationService = new CancellationService(inventory, history);

        // Cancel Alice’s reservation
        System.out.println("\n--- Guest Alice initiates cancellation ---");
        cancellationService.cancelReservation(r1.getReservationId());

        // Display updated state
        history.displayHistory();
        inventory.displayInventory();
        cancellationService.displayRollbackStack();
    }
}