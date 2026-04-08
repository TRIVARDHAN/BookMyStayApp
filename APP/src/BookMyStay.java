import java.io.*;
import java.util.*;

// Reservation Class (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    String reservationId;
    String guestName;
    String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}

// Wrapper class for persistence
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    List<Reservation> reservations;
    Map<String, Integer> inventory;

    public SystemState(List<Reservation> reservations, Map<String, Integer> inventory) {
        this.reservations = reservations;
        this.inventory = inventory;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "bookmyStay.dat";

    // Save state
    public void save(SystemState state) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(state);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    // Load state
    public SystemState load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("System state loaded successfully.");
            return (SystemState) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Data corrupted. Starting with clean state.");
        }
        return null;
    }
}

// Main Class
public class BookMyStay {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();

        List<Reservation> reservations;
        Map<String, Integer> inventory;

        // Load previous state
        SystemState state = persistence.load();

        if (state != null) {
            reservations = state.reservations;
            inventory = state.inventory;
        } else {
            // Initialize fresh state
            reservations = new ArrayList<>();
            inventory = new HashMap<>();
            inventory.put("Standard", 2);
            inventory.put("Deluxe", 1);
            inventory.put("Suite", 1);
        }

        Scanner scanner = new Scanner(System.in);
        int counter = reservations.size() + 100;

        while (true) {
            System.out.println("\n=== BookMyStay System ===");
            System.out.println("1. Book Room");
            System.out.println("2. View Reservations");
            System.out.println("3. View Inventory");
            System.out.println("4. Exit (Save)");

            int choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    System.out.print("Enter Guest Name: ");
                    String name = scanner.next();

                    System.out.print("Enter Room Type (Standard/Deluxe/Suite): ");
                    String type = scanner.next();

                    if (!inventory.containsKey(type)) {
                        System.out.println("Invalid room type.");
                        break;
                    }

                    if (inventory.get(type) <= 0) {
                        System.out.println("Room not available.");
                        break;
                    }

                    inventory.put(type, inventory.get(type) - 1);
                    String resId = "RES" + (++counter);

                    reservations.add(new Reservation(resId, name, type));

                    System.out.println("Booking confirmed: " + resId);
                    break;

                case 2:
                    System.out.println("\nReservations:");
                    for (Reservation r : reservations) {
                        System.out.println(r);
                    }
                    break;

                case 3:
                    System.out.println("\nInventory:");
                    for (String key : inventory.keySet()) {
                        System.out.println(key + " -> " + inventory.get(key));
                    }
                    break;

                case 4:
                    // Save before exit
                    persistence.save(new SystemState(reservations, inventory));
                    System.out.println("Exiting system...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}