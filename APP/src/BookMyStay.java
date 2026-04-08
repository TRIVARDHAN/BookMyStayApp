import java.util.*;

// Represents an Add-On Service
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
    }
}

// Manages Add-On Services for Reservations
class AddOnServiceManager {

    // Map: Reservation ID -> List of Services
    private Map<String, List<AddOnService>> reservationServicesMap;

    public AddOnServiceManager() {
        reservationServicesMap = new HashMap<>();
    }

    // Add service to a reservation
    public void addService(String reservationId, AddOnService service) {
        reservationServicesMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    // Get services for a reservation
    public List<AddOnService> getServices(String reservationId) {
        return reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total cost of add-ons
    public double calculateTotalServiceCost(String reservationId) {
        double total = 0;
        List<AddOnService> services = getServices(reservationId);

        for (AddOnService service : services) {
            total += service.getCost();
        }
        return total;
    }
}

// Main Class
public class BookMyStay {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        AddOnServiceManager manager = new AddOnServiceManager();

        // Sample reservation (core booking untouched)
        String reservationId = "RES123";

        System.out.println("=== Add-On Service Selection ===");
        System.out.println("Reservation ID: " + reservationId);

        while (true) {
            System.out.println("\nChoose Add-On Service:");
            System.out.println("1. Breakfast (₹500)");
            System.out.println("2. Airport Pickup (₹1200)");
            System.out.println("3. Extra Bed (₹800)");
            System.out.println("4. Finish");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    manager.addService(reservationId, new AddOnService("Breakfast", 500));
                    System.out.println("Breakfast added.");
                    break;

                case 2:
                    manager.addService(reservationId, new AddOnService("Airport Pickup", 1200));
                    System.out.println("Airport Pickup added.");
                    break;

                case 3:
                    manager.addService(reservationId, new AddOnService("Extra Bed", 800));
                    System.out.println("Extra Bed added.");
                    break;

                case 4:
                    System.out.println("\nFinal Add-On Services:");
                    List<AddOnService> services = manager.getServices(reservationId);

                    for (AddOnService s : services) {
                        System.out.println("- " + s);
                    }

                    double totalCost = manager.calculateTotalServiceCost(reservationId);
                    System.out.println("Total Add-On Cost: ₹" + totalCost);

                    System.out.println("\nBooking remains unchanged. Add-ons applied successfully.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}