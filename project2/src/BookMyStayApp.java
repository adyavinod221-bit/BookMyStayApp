/**
 * UseCase11ConcurrentBookingSimulation.java
 *
 * This class demonstrates Use Case 11 of the Book My Stay App:
 * Concurrent Booking Simulation (Thread Safety).
 * Multiple guests submit booking requests concurrently, and
 * synchronized access ensures correct inventory updates.
 *
 * @author YourName
 * @version 11.1
 */

import java.util.*;

// Reservation class
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return "Reservation [Guest: " + guestName + ", Room Type: " + roomType + "]";
    }
}

// RoomInventory with synchronized methods
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {}

    public void addRoomType(String roomType, int availability) {
        inventory.put(roomType, availability);
    }

    public synchronized boolean allocateRoom(String roomType, String guestName) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available > 0) {
            inventory.put(roomType, available - 1);
            System.out.println("Confirmed: " + guestName + " booked " + roomType +
                    " | Remaining: " + (available - 1));
            return true;
        } else {
            System.out.println("Failed: " + guestName + " requested " + roomType +
                    " but no availability.");
            return false;
        }
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

// BookingProcessor runs in separate threads
class BookingProcessor implements Runnable {
    private Reservation reservation;
    private RoomInventory inventory;

    public BookingProcessor(Reservation reservation, RoomInventory inventory) {
        this.reservation = reservation;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        inventory.allocateRoom(reservation.getRoomType(), reservation.getGuestName());
    }
}

// Application Entry Point
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App!");
        System.out.println("   Hotel Booking System v11.1");
        System.out.println("=======================================\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);

        // Simulate concurrent booking requests
        List<Reservation> reservations = Arrays.asList(
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Single Room"),
                new Reservation("Charlie", "Single Room"), // should fail
                new Reservation("Diana", "Double Room"),
                new Reservation("Eve", "Double Room")      // should fail
        );

        // Create threads for each reservation
        List<Thread> threads = new ArrayList<>();
        for (Reservation r : reservations) {
            Thread t = new Thread(new BookingProcessor(r, inventory));
            threads.add(t);
            t.start();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Display final inventory state
        inventory.displayInventory();
    }
}