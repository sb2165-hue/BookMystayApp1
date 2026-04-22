import java.util.*;

// Booking Request
class BookingRequest {
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// Shared Inventory (CRITICAL RESOURCE)
class InventoryManager {
    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryManager() {
        inventory.put("Single", 2);
        inventory.put("Double", 1);
    }

    // Critical Section (Thread Safe)
    public synchronized boolean allocateRoom(String roomType, String guestName) {
        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            System.out.println(Thread.currentThread().getName() +
                    " allocating " + roomType + " to " + guestName);

            // Simulate delay (to expose race condition if not synchronized)
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            inventory.put(roomType, available - 1);
            return true;
        } else {
            System.out.println(Thread.currentThread().getName() +
                    " FAILED for " + guestName + " (No rooms)");
            return false;
        }
    }

    public void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " Rooms: " + inventory.get(type));
        }
    }
}

// Shared Booking Queue
class BookingQueue {
    private Queue<BookingRequest> queue = new LinkedList<>();

    // Thread-safe add
    public synchronized void addRequest(BookingRequest request) {
        queue.add(request);
    }

    // Thread-safe retrieval
    public synchronized BookingRequest getRequest() {
        return queue.poll();
    }
}

// Worker Thread (Concurrent Booking Processor)
class BookingProcessor extends Thread {
    private BookingQueue queue;
    private InventoryManager inventory;

    public BookingProcessor(String name, BookingQueue queue, InventoryManager inventory) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest request;

            // Get request safely
            synchronized (queue) {
                request = queue.getRequest();
            }

            if (request == null) break;

            // Critical booking operation
            inventory.allocateRoom(request.roomType, request.guestName);
        }
    }
}

// Main Class
public class bookmystay {
    public static void main(String[] args) {

        InventoryManager inventory = new InventoryManager();
        BookingQueue queue = new BookingQueue();

        // Simulate multiple guest requests (same time)
        queue.addRequest(new BookingRequest("Guest1", "Single"));
        queue.addRequest(new BookingRequest("Guest2", "Single"));
        queue.addRequest(new BookingRequest("Guest3", "Single")); // extra (should fail)
        queue.addRequest(new BookingRequest("Guest4", "Double"));
        queue.addRequest(new BookingRequest("Guest5", "Double")); // extra (should fail)

        // Multiple threads (simulating concurrent users)
        BookingProcessor t1 = new BookingProcessor("Thread-1", queue, inventory);
        BookingProcessor t2 = new BookingProcessor("Thread-2", queue, inventory);
        BookingProcessor t3 = new BookingProcessor("Thread-3", queue, inventory);

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {}

        // Final state
        inventory.displayInventory();
    }
}