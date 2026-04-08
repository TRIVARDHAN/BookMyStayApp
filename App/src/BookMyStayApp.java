import java.util.*;

// Booking Request Model
class BookingRequest {
    String customerName;
    String roomType;

    public BookingRequest(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }
}

// Inventory Service
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public synchronized boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public synchronized void decrement(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + inventory);
    }
}

// Booking Service
class BookingService {

    private Queue<BookingRequest> requestQueue = new LinkedList<>();
    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Set<String>> roomTypeMap = new HashMap<>();
    private InventoryService inventoryService;

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void addRequest(BookingRequest request) {
        requestQueue.offer(request);
    }

    // Generate unique Room ID
    private String generateRoomId(String roomType) {
        return roomType.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);
    }

    // Core Allocation Logic
    public void processBookings() {

        while (!requestQueue.isEmpty()) {

            BookingRequest request = requestQueue.poll();
            String roomType = request.roomType;

            synchronized (this) {

                if (inventoryService.isAvailable(roomType)) {

                    String roomId;

                    // Ensure uniqueness
                    do {
                        roomId = generateRoomId(roomType);
                    } while (allocatedRoomIds.contains(roomId));

                    // Assign room
                    allocatedRoomIds.add(roomId);

                    roomTypeMap.putIfAbsent(roomType, new HashSet<>());
                    roomTypeMap.get(roomType).add(roomId);

                    // Update inventory immediately
                    inventoryService.decrement(roomType);

                    // Confirmation
                    System.out.println("Booking Confirmed for " + request.customerName +
                            " | Room Type: " + roomType +
                            " | Room ID: " + roomId);
                } else {
                    System.out.println("Booking Failed for " + request.customerName +
                            " | Room Type: " + roomType + " is not available.");
                }
            }
        }
    }

    public void displayAllocations() {
        System.out.println("\nRoom Allocations:");
        for (String type : roomTypeMap.keySet()) {
            System.out.println(type + " -> " + roomTypeMap.get(type));
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingService bookingService = new BookingService(inventoryService);

        // Add booking requests (FIFO)
        bookingService.addRequest(new BookingRequest("Alice", "Single"));
        bookingService.addRequest(new BookingRequest("Bob", "Double"));
        bookingService.addRequest(new BookingRequest("Charlie", "Single"));
        bookingService.addRequest(new BookingRequest("David", "Suite"));
        bookingService.addRequest(new BookingRequest("Eve", "Suite")); // Should fail

        // Process Bookings
        bookingService.processBookings();

        // Display Results
        bookingService.displayAllocations();
        inventoryService.displayInventory();
    }
}