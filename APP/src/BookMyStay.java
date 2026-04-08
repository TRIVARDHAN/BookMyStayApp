import java.util.*;

// Domain Model: Room
class Room {
    private String type;
    private double price;
    private List<String> amenities;

    public Room(String type, double price, List<String> amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Price: ₹" + price);
        System.out.println("Amenities: " + amenities);
        System.out.println("---------------------------");
    }
}

// Inventory: Holds availability (STATE HOLDER)
class Inventory {
    private Map<String, Integer> roomAvailability;

    public Inventory() {
        roomAvailability = new HashMap<>();
    }

    public void addRoom(String type, int count) {
        roomAvailability.put(type, count);
    }

    // READ-ONLY access
    public int getAvailability(String type) {
        return roomAvailability.getOrDefault(type, 0);
    }

    // Return a copy to prevent mutation (Defensive Programming)
    public Map<String, Integer> getAllAvailability() {
        return new HashMap<>(roomAvailability);
    }
}

// Search Service: Handles read-only operations
class SearchService {

    public void searchAvailableRooms(Inventory inventory, Map<String, Room> roomCatalog) {
        System.out.println("Available Rooms:\n");

        Map<String, Integer> availability = inventory.getAllAvailability();

        boolean found = false;

        for (String type : availability.keySet()) {
            int count = availability.get(type);

            // Validation Logic: Only show available rooms
            if (count > 0) {
                Room room = roomCatalog.get(type);

                if (room != null) { // Defensive check
                    room.displayDetails();
                    System.out.println("Available Count: " + count);
                    System.out.println("===========================");
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("No rooms available at the moment.");
        }
    }
}

// Main Class
public class BookMyStay {

    public static void main(String[] args) {

        // Create Room Catalog (Domain Layer)
        Map<String, Room> roomCatalog = new HashMap<>();

        roomCatalog.put("Single", new Room(
                "Single",
                2000,
                Arrays.asList("WiFi", "AC")
        ));

        roomCatalog.put("Double", new Room(
                "Double",
                3500,
                Arrays.asList("WiFi", "AC", "TV")
        ));

        roomCatalog.put("Suite", new Room(
                "Suite",
                5000,
                Arrays.asList("WiFi", "AC", "TV", "Mini Bar")
        ));

        // Setup Inventory
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 3);
        inventory.addRoom("Double", 0);  // Should NOT appear
        inventory.addRoom("Suite", 2);

        // Perform Search (Read-Only)
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(inventory, roomCatalog);
    }
}