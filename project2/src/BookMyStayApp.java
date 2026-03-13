/**
 * UseCase6RoomAllocationService.java
 *
 * This class demonstrates Use Case 6 of the Book My Stay App:
 * Reservation Confirmation & Room Allocation.
 * Booking requests are processed from a queue, unique room IDs are assigned,
 * and inventory is updated immediately to prevent double-booking.
 *
 * @author YourName
 * @version 6.1
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

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation [Guest: " + guestName + ", Room Type: " + roomType + "]";
    }
}

// Centralized Inventory Service
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int availability) {
        inventory.put(roomType, availability);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public boolean decrementAvailability(String roomType) {
        int current = getAvailability(roomType);
        if (current > 0) {
            inventory.put(roomType, current - 1);
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("=== Current Room Inventory ===");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println("Room Type: " + entry.getKey() +
                    " | Availability: " + entry.getValue());
        }
        System.out.println("===============================");
    }
}

// Booking Service handles allocation
class BookingService {
    private Queue<Reservation> requestQueue;
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms; // roomType -> roomIDs

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.requestQueue = new LinkedList<>();
        this.allocatedRooms = new HashMap<>();
    }

    // Add request to queue
    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Request queued: " + reservation);
    }

    // Process requests in FIFO order
    public void processRequests() {
        System.out.println("\n=== Processing Booking Requests ===");
        while (!requestQueue.isEmpty()) {
            Reservation reservation = requestQueue.poll();
            String roomType = reservation.getRoomType();

            if (inventory.decrementAvailability(roomType)) {
                // Generate unique room ID
                String roomId = UUID.randomUUID().toString();

                // Ensure uniqueness using Set
                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(roomId);

                System.out.println("Confirmed: " + reservation.getGuestName() +
                        " booked " + roomType +
                        " | Room ID: " + roomId);
            } else {
                System.out.println("Failed: " + reservation.getGuestName() +
                        " requested " + roomType +
                        " but no availability.");
            }
        }
        System.out.println("===================================");
    }

    // Display allocated rooms
    public void displayAllocations() {
        System.out.println("\n=== Allocated Rooms ===");
        for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {
            System.out.println("Room Type: " + entry.getKey() +
                    " | Room IDs: " + entry.getValue());
        }
        System.out.println("========================");
    }
}

// Application Entry Point
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App!");
        System.out.println("   Hotel Booking System v6.1");
        System.out.println("=======================================\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        // Initialize booking service
        BookingService bookingService = new BookingService(inventory);

        // Simulate booking requests
        bookingService.addRequest(new Reservation("Alice", "Single Room"));
        bookingService.addRequest(new Reservation("Bob", "Suite Room"));
        bookingService.addRequest(new Reservation("Charlie", "Single Room"));
        bookingService.addRequest(new Reservation("Diana", "Double Room"));
        bookingService.addRequest(new Reservation("Eve", "Single Room")); // should fail

        // Process requests
        bookingService.processRequests();

        // Display allocations and updated inventory
        bookingService.displayAllocations();
        inventory.displayInventory();
    }
}