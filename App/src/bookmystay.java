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
}

// Add-On Service class
class AddOnService {
    private String serviceName;
    private double serviceCost;

    public AddOnService(String serviceName, double serviceCost) {
        this.serviceName = serviceName;
        this.serviceCost = serviceCost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getServiceCost() {
        return serviceCost;
    }
}

// Add-On Service Manager
class AddOnServiceManager {
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }

    // Get services for reservation
    public List<AddOnService> getServices(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total add-on cost
    public double calculateServiceCost(String reservationId) {
        double total = 0;
        List<AddOnService> services = getServices(reservationId);

        for (AddOnService s : services) {
            total += s.getServiceCost();
        }

        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        List<AddOnService> services = getServices(reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        System.out.println("Add-On Services:");
        for (AddOnService s : services) {
            System.out.println("- " + s.getServiceName() + " : ₹" + s.getServiceCost());
        }
    }
}

// Main Application Class
public class bookmystay {
    public static void main(String[] args) {

        // Create reservation (core booking)
        Reservation res1 = new Reservation("R101", "Srinivasa Rao", 2000);

        // Create service manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Guest selects add-on services
        manager.addService("R101", new AddOnService("Breakfast", 300));
        manager.addService("R101", new AddOnService("Airport Pickup", 800));
        manager.addService("R101", new AddOnService("Extra Bed", 500));

        // Display reservation details
        System.out.println("Reservation ID: " + res1.getReservationId());
        System.out.println("Guest Name: " + res1.getGuestName());
        System.out.println("Base Price: ₹" + res1.getBasePrice());

        // Display add-on services
        manager.displayServices("R101");

        // Calculate total cost
        double addOnCost = manager.calculateServiceCost("R101");
        double finalCost = res1.getBasePrice() + addOnCost;

        System.out.println("Add-On Cost: ₹" + addOnCost);
        System.out.println("Total Cost: ₹" + finalCost);
    }
}