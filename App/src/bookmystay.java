import java.util.*;

// Custom Exception
class InvalidCancellationException extends Exception {
    public InvalidCancellationException(String message) {
        super(message);
    }
}

// Reservation class
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType +
                " | RoomID: " + roomId + " | Cancelled: " + isCancelled;
    }
}

// Inventory Manager
class InventoryManager {
    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryManager() {
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public void allocateRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void releaseRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) + 1);
    }

    public void displayInventory() {
        System.out.println("\nInventory Status:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " Rooms: " + inventory.get(type));
        }
    }
}

// Booking History
class BookingHistory {
    private Map<String, Reservation> bookings = new HashMap<>();

    public void addReservation(Reservation r) {
        bookings.put(r.getReservationId(), r);
    }

    public Reservation getReservation(String id) {
        return bookings.get(id);
    }

    public void displayAll() {
        System.out.println("\n--- Booking History ---");
        for (Reservation r : bookings.values()) {
            System.out.println(r);
        }
    }
}

// Cancellation Service (Core Logic)
class CancellationService {
    private Stack<String> rollbackStack = new Stack<>();

    public void cancelBooking(String reservationId,
                              BookingHistory history,
                              InventoryManager inventory)
            throws InvalidCancellationException {

        // Step 1: Validate existence
        Reservation reservation = history.getReservation(reservationId);

        if (reservation == null) {
            throw new InvalidCancellationException("Reservation does not exist.");
        }

        // Step 2: Check already cancelled
        if (reservation.isCancelled()) {
            throw new InvalidCancellationException("Booking already cancelled.");
        }

        // Step 3: Record rollback (LIFO)
        rollbackStack.push(reservation.getRoomId());

        // Step 4: Restore inventory
        inventory.releaseRoom(reservation.getRoomType());

        // Step 5: Mark as cancelled
        reservation.cancel();

        System.out.println("Cancellation successful for: " + reservationId);
        System.out.println("Released Room ID (tracked in stack): " + rollbackStack.peek());
    }
}

// Main Class
public class bookmystay {
    public static void main(String[] args) {

        InventoryManager inventory = new InventoryManager();
        BookingHistory history = new BookingHistory();
        CancellationService cancelService = new CancellationService();

        // Simulate confirmed bookings
        Reservation r1 = new Reservation("R101", "Srinivasa Rao", "Single", "S1");
        Reservation r2 = new Reservation("R102", "Teja", "Double", "D1");

        history.addReservation(r1);
        history.addReservation(r2);

        // Allocate rooms (simulate earlier booking)
        inventory.allocateRoom("Single");
        inventory.allocateRoom("Double");

        try {
            // VALID cancellation
            cancelService.cancelBooking("R101", history, inventory);

            // INVALID: already cancelled
            cancelService.cancelBooking("R101", history, inventory);

        } catch (InvalidCancellationException e) {
            System.out.println("Cancellation Failed: " + e.getMessage());
        }

        try {
            // INVALID: non-existent booking
            cancelService.cancelBooking("R999", history, inventory);

        } catch (InvalidCancellationException e) {
            System.out.println("Cancellation Failed: " + e.getMessage());
        }

        // Final system state
        history.displayAll();
        inventory.displayInventory();
    }
}