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

// Shared Booking System
class ConcurrentBookingProcessor {

    private Queue<BookingRequest> bookingQueue = new LinkedList<>();
    private Map<String, Integer> inventory = new HashMap<>();

    public ConcurrentBookingProcessor() {
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
        inventory.put("Suite", 1);
    }

    // Add booking request (producer)
    public synchronized void addRequest(BookingRequest request) {
        bookingQueue.add(request);
        System.out.println("Request added: " + request.guestName + " -> " + request.roomType);
    }

    // Process booking request (consumer)
    public void processRequest() {
        BookingRequest request;

        // Critical section: retrieve request
        synchronized (this) {
            if (bookingQueue.isEmpty()) {
                return;
            }
            request = bookingQueue.poll();
        }

        // Critical section: allocate room
        synchronized (this) {
            String type = request.roomType;

            if (!inventory.containsKey(type)) {
                System.out.println("Invalid room type for " + request.guestName);
                return;
            }

            if (inventory.get(type) > 0) {
                inventory.put(type, inventory.get(type) - 1);
                System.out.println("Booking SUCCESS for " + request.guestName + " (" + type + ")");
            } else {
                System.out.println("Booking FAILED for " + request.guestName + " (" + type + " - Not Available)");
            }
        }
    }

    public void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

// Worker Thread
class BookingWorker extends Thread {

    private ConcurrentBookingProcessor processor;

    public BookingWorker(ConcurrentBookingProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void run() {
        // Each thread tries multiple times
        for (int i = 0; i < 2; i++) {
            processor.processRequest();
            try {
                Thread.sleep(100); // simulate delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Main Class
public class BookMyStay {

    public static void main(String[] args) {

        ConcurrentBookingProcessor processor = new ConcurrentBookingProcessor();

        // Simulate multiple booking requests
        processor.addRequest(new BookingRequest("Arun", "Standard"));
        processor.addRequest(new BookingRequest("Priya", "Standard"));
        processor.addRequest(new BookingRequest("Kiran", "Standard")); // extra request
        processor.addRequest(new BookingRequest("Sneha", "Deluxe"));
        processor.addRequest(new BookingRequest("Rahul", "Suite"));

        // Create multiple threads (guests)
        Thread t1 = new BookingWorker(processor);
        Thread t2 = new BookingWorker(processor);
        Thread t3 = new BookingWorker(processor);

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        processor.displayInventory();
        System.out.println("\nAll bookings processed safely.");
    }
}