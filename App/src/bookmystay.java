import java.util.*;

// Custom Exception for invalid booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation class
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private double price;

    public Reservation(String reservationId, String guestName, String roomType, double price) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType + " | ₹" + price;
    }
}

// Inventory Manager (critical state)
class InventoryManager {
    private Map<String, Integer> roomInventory = new HashMap<>();

    public InventoryManager() {
        roomInventory.put("Single", 2);
        roomInventory.put("Double", 2);
        roomInventory.put("Suite", 1);
    }

    public boolean isRoomTypeValid(String roomType) {
        return roomInventory.containsKey(roomType);
    }

    public int getAvailableRooms(String roomType) {
        return roomInventory.getOrDefault(roomType, 0);
    }

    public void reduceRoom(String roomType) throws InvalidBookingException {
        int available = getAvailableRooms(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + roomType);
        }

        roomInventory.put(roomType, available - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : roomInventory.entrySet()) {
            System.out.println(entry.getKey() + " Rooms: " + entry.getValue());
        }
    }
}

// Validator (Fail-Fast)
class InvalidBookingValidator {

    public static void validate(String guestName, String roomType, InventoryManager inventory)
            throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (!inventory.isRoomTypeValid(roomType)) {
            throw new InvalidBookingException("Invalid room type selected: " + roomType);
        }

        if (inventory.getAvailableRooms(roomType) <= 0) {
            throw new InvalidBookingException("Selected room type is fully booked.");
        }
    }
}

// Booking Service
class BookingService {
    private InventoryManager inventory;

    public BookingService(InventoryManager inventory) {
        this.inventory = inventory;
    }

    public Reservation createBooking(String id, String guestName, String roomType, double price)
            throws InvalidBookingException {

        // Step 1: Validate input (Fail-Fast)
        InvalidBookingValidator.validate(guestName, roomType, inventory);

        // Step 2: Update inventory safely
        inventory.reduceRoom(roomType);

        // Step 3: Create reservation
        return new Reservation(id, guestName, roomType, price);
    }
}

// Main Class
public class bookmystay {
    public static void main(String[] args) {

        InventoryManager inventory = new InventoryManager();
        BookingService bookingService = new BookingService(inventory);

        try {
            // VALID BOOKING
            Reservation r1 = bookingService.createBooking("R101", "Srinivasa Rao", "Single", 2000);
            System.out.println("Booking Successful: " + r1);

            // INVALID ROOM TYPE
            Reservation r2 = bookingService.createBooking("R102", "Teja", "Luxury", 3000);
            System.out.println("Booking Successful: " + r2);

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("Booking Failed: " + e.getMessage());
        }

        try {
            // INVALID (No rooms left scenario)
            bookingService.createBooking("R103", "Rahul", "Suite", 5000);
            bookingService.createBooking("R104", "Kiran", "Suite", 5000); // should fail

        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }

        // System still running safely
        inventory.displayInventory();
    }
}