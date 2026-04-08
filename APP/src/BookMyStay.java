import java.util.*;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation Class
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType;
    }
}

// Validator Class
class InvalidBookingValidator {

    private Map<String, Integer> inventory;

    public InvalidBookingValidator() {
        inventory = new HashMap<>();
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
        inventory.put("Suite", 1);
    }

    // Validate booking request
    public void validate(String roomType) throws InvalidBookingException {

        // Validate room type
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        // Validate availability
        if (inventory.get(roomType) <= 0) {
            throw new InvalidBookingException("No rooms available for " + roomType);
        }
    }

    // Update inventory safely
    public void allocateRoom(String roomType) throws InvalidBookingException {
        int count = inventory.get(roomType);

        if (count <= 0) {
            throw new InvalidBookingException("Cannot allocate. Inventory is already zero.");
        }

        inventory.put(roomType, count - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

// Main Class
public class BookMyStay {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        InvalidBookingValidator validator = new InvalidBookingValidator();

        int bookingCounter = 100;

        while (true) {
            try {
                System.out.println("\n=== Booking System ===");
                validator.displayInventory();

                System.out.print("Enter Guest Name: ");
                String guestName = scanner.next();

                System.out.print("Enter Room Type (Standard/Deluxe/Suite): ");
                String roomType = scanner.next();

                // Validation (Fail-Fast)
                validator.validate(roomType);

                // Allocation
                validator.allocateRoom(roomType);

                String reservationId = "RES" + (++bookingCounter);
                Reservation reservation = new Reservation(reservationId, guestName, roomType);

                System.out.println("Booking Confirmed: " + reservation);

            } catch (InvalidBookingException e) {
                // Graceful failure handling
                System.out.println("Booking Failed: " + e.getMessage());
            } catch (Exception e) {
                // Catch unexpected errors
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
}