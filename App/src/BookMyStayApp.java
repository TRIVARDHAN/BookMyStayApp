import java.util.*;

// Reservation: Represents booking intent
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void display() {
        System.out.println("Guest: " + guestName + " | Requested Room: " + roomType);
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    // Add request (enqueue)
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request added for " + reservation.getGuestName());
    }

    // View all requests (without removing)
    public void displayQueue() {
        System.out.println("\nBooking Request Queue (FIFO Order):");

        if (queue.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        for (Reservation r : queue) {
            r.display();
        }
    }

    // Peek next request (no removal)
    public Reservation peekNext() {
        return queue.peek();
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // Initialize booking queue
        BookingRequestQueue BookMyStay  = new BookingRequestQueue();

        // Simulate multiple guest requests (arrival order matters)
        BookMyStay .addRequest(new Reservation("Alice", "Single"));
        BookMyStay .addRequest(new Reservation("Bob", "Double"));
        BookMyStay .addRequest(new Reservation("Charlie", "Suite"));
        BookMyStay .addRequest(new Reservation("Diana", "Single"));

        // Display queue (FIFO order)
        BookMyStay .displayQueue();

        // Show next request to be processed (without removing)
        Reservation next = BookMyStay .peekNext();

        System.out.println("\nNext Request to Process:");
        if (next != null) {
            next.display();
        } else {
            System.out.println("No requests available.");
        }
    }
}