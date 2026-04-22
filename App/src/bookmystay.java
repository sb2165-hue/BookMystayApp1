import java.io.*;
import java.util.*;

// Reservation class (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}

// Inventory Manager (Serializable)
class InventoryManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryManager() {
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public void allocateRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public void displayInventory() {
        System.out.println("\nInventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " Rooms: " + inventory.get(type));
        }
    }
}

// Booking History (Serializable)
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> bookings = new ArrayList<>();

    public void addReservation(Reservation r) {
        bookings.add(r);
    }

    public List<Reservation> getBookings() {
        return bookings;
    }

    public void displayBookings() {
        System.out.println("\nBooking History:");
        for (Reservation r : bookings) {
            System.out.println(r);
        }
    }
}

// Wrapper class to persist entire system state
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    InventoryManager inventory;
    BookingHistory history;

    public SystemState(InventoryManager inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.ser";

    // Save state
    public static void save(SystemState state) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(state);
            System.out.println("\nSystem state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    // Load state
    public static SystemState load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("System state loaded successfully.");
            return (SystemState) in.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No previous state found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Error loading state. Starting with safe defaults.");
        }

        // Safe fallback
        return new SystemState(new InventoryManager(), new BookingHistory());
    }
}

// Main Class
public class bookmystay {
    public static void main(String[] args) {

        // Step 1: Load previous state (Recovery)
        SystemState state = PersistenceService.load();

        InventoryManager inventory = state.inventory;
        BookingHistory history = state.history;

        // Step 2: Simulate operations
        Reservation r1 = new Reservation("R101", "Srinivasa Rao", "Single");
        history.addReservation(r1);
        inventory.allocateRoom("Single");

        Reservation r2 = new Reservation("R102", "Teja", "Double");
        history.addReservation(r2);
        inventory.allocateRoom("Double");

        // Step 3: Display current state
        history.displayBookings();
        inventory.displayInventory();

        // Step 4: Save state before shutdown
        PersistenceService.save(new SystemState(inventory, history));
    }
}