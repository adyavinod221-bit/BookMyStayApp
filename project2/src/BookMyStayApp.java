/**
 * UseCase12DataPersistenceRecovery.java
 *
 * This class demonstrates Use Case 12 of the Book My Stay App:
 * Data Persistence & System Recovery.
 * Booking history and inventory state are serialized to a file
 * and restored during application restart.
 *
 * @author YourName
 * @version 12.1
 */

import java.io.*;
import java.util.*;

// Reservation class (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.reservationId = UUID.randomUUID().toString();
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return "Reservation [ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType + "]";
    }
}

// RoomInventory class (Serializable)
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String roomType, int availability) {
        inventory.put(roomType, availability);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrementAvailability(String roomType) {
        int current = getAvailability(roomType);
        if (current > 0) inventory.put(roomType, current - 1);
    }

    public void incrementAvailability(String roomType) {
        inventory.put(roomType, getAvailability(roomType) + 1);
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

// BookingHistory class (Serializable)
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        history.add(reservation);
        System.out.println("Reservation confirmed: " + reservation);
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(history);
    }

    public void displayHistory() {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : history) System.out.println(r);
        System.out.println("========================");
    }
}

// PersistenceService handles saving and loading
class PersistenceService {
    private static final String FILE_NAME = "bookingData.ser";

    public static void saveData(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(history);
            System.out.println("\nSystem state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public static Object[] loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            RoomInventory inventory = (RoomInventory) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();
            System.out.println("\nSystem state restored successfully.");
            return new Object[]{inventory, history};
        } catch (FileNotFoundException e) {
            System.out.println("\nNo persistence file found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
        return new Object[]{new RoomInventory(), new BookingHistory()};
    }
}

// Application Entry Point
public class BookMyStayApp{
    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App!");
        System.out.println("   Hotel Booking System v12.1");
        System.out.println("=======================================\n");

        // Load persisted state if available
        Object[] restoredData = PersistenceService.loadData();
        RoomInventory inventory = (RoomInventory) restoredData[0];
        BookingHistory history = (BookingHistory) restoredData[1];

        // If fresh start, initialize inventory
        if (inventory.getAvailability("Single Room") == 0 &&
                inventory.getAvailability("Double Room") == 0) {
            inventory.addRoomType("Single Room", 2);
            inventory.addRoomType("Double Room", 1);
        }

        // Simulate new reservations
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Double Room");

        history.addReservation(r1);
        inventory.decrementAvailability(r1.getRoomType());

        history.addReservation(r2);
        inventory.decrementAvailability(r2.getRoomType());

        // Display current state
        history.displayHistory();
        inventory.displayInventory();

        // Save state before shutdown
        PersistenceService.saveData(inventory, history);
    }
}