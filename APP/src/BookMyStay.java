import java.util.*;

// Custom Exception
class CancellationException extends Exception {
    public CancellationException(String message) {
        super(message);
    }
}

// Reservation Class
class Reservation {
    String reservationId;
    String guestName;
    String roomType;
    String roomId;
    boolean isCancelled;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType +
                ", Room ID: " + roomId +
                ", Status: " + (isCancelled ? "Cancelled" : "Active");
    }
}

// Cancellation Service
class CancellationService {

    private Map<String, Reservation> reservationMap;
    private Map<String, Integer> inventory;
    private Stack<String> rollbackStack;

    public CancellationService() {
        reservationMap = new HashMap<>();
        inventory = new HashMap<>();
        rollbackStack = new Stack<>();

        // Initial inventory
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
        inventory.put("Suite", 1);

        // Sample confirmed bookings
        addSampleBooking("RES201", "Arun", "Standard", "S1");
        addSampleBooking("RES202", "Priya", "Deluxe", "D1");
    }

    private void addSampleBooking(String id, String name, String type, String roomId) {
        Reservation r = new Reservation(id, name, type, roomId);
        reservationMap.put(id, r);
        inventory.put(type, inventory.get(type) - 1); // simulate allocation
    }

    // Cancel booking
    public void cancelBooking(String reservationId) throws CancellationException {

        // Validate existence
        if (!reservationMap.containsKey(reservationId)) {
            throw new CancellationException("Reservation does not exist.");
        }

        Reservation res = reservationMap.get(reservationId);

        // Prevent duplicate cancellation
        if (res.isCancelled) {
            throw new CancellationException("Booking already cancelled.");
        }

        // --- Controlled Rollback ---
        // Step 1: Track released room (LIFO)
        rollbackStack.push(res.roomId);

        // Step 2: Restore inventory
        inventory.put(res.roomType, inventory.get(res.roomType) + 1);

        // Step 3: Update booking status
        res.isCancelled = true;

        System.out.println("Cancellation successful for: " + reservationId);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }

    public void displayReservations() {
        System.out.println("\nReservations:");
        for (Reservation r : reservationMap.values()) {
            System.out.println(r);
        }
    }

    public void displayRollbackStack() {
        System.out.println("\nRollback Stack (Recently Released Rooms): " + rollbackStack);
    }
}

// Main Class
public class BookMyStay {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        CancellationService service = new CancellationService();

        while (true) {
            try {
                System.out.println("\n=== Booking Cancellation System ===");

                service.displayReservations();
                service.displayInventory();

                System.out.print("\nEnter Reservation ID to cancel: ");
                String resId = scanner.next();

                service.cancelBooking(resId);

                service.displayRollbackStack();

            } catch (CancellationException e) {
                System.out.println("Cancellation Failed: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error occurred.");
            }

            System.out.print("\nDo you want to continue? (yes/no): ");
            String choice = scanner.next();

            if (choice.equalsIgnoreCase("no")) {
                System.out.println("Exiting system...");
                break;
            }
        }

        scanner.close();
    }
}1