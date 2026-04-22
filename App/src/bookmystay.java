import java.util.*;

// Core Reservation class (UNCHANGED)
class Reservation {
    private String reservationId;
    private String guestName;
    private double basePrice;

    public Reservation(String reservationId, String guestName, double basePrice) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.basePrice = basePrice;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public double getBasePrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Price: ₹" + basePrice;
    }
}

// Booking History (stores confirmed bookings)
class BookingHistory {
    private List<Reservation> confirmedBookings = new ArrayList<>();

    // Add confirmed booking
    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    // Retrieve all bookings
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings); // return copy (safe)
    }
}

// Booking Report Service (read-only operations)
class BookingReportService {

    // Display all bookings
    public void displayAllBookings(List<Reservation> reservations) {
        if (reservations.isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }

        System.out.println("\n--- Booking History ---");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    // Generate summary report
    public void generateSummary(List<Reservation> reservations) {
        int totalBookings = reservations.size();
        double totalRevenue = 0;

        for (Reservation r : reservations) {
            totalRevenue += r.getBasePrice();
        }

        System.out.println("\n--- Booking Summary Report ---");
        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Revenue: ₹" + totalRevenue);
    }
}

// Main Application
public class bookmystay {
    public static void main(String[] args) {

        // Booking history storage
        BookingHistory history = new BookingHistory();

        // Simulate confirmed bookings
        Reservation r1 = new Reservation("R101", "Srinivasa Rao", 2000);
        Reservation r2 = new Reservation("R102", "Teja", 3000);
        Reservation r3 = new Reservation("R103", "Rahul", 2500);

        // Step 1: Confirm bookings → store in history
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);

        // Step 2: Admin retrieves booking history
        List<Reservation> storedBookings = history.getAllReservations();

        // Step 3: Generate reports
        BookingReportService reportService = new BookingReportService();

        reportService.displayAllBookings(storedBookings);
        reportService.generateSummary(storedBookings);
    }
}