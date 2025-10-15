import java.util.*;

class Venue {
    String name;
    boolean isBooked;

    Venue(String name) {
        this.name = name;
        this.isBooked = false;
    }

    void book() {
        isBooked = true;
    }

    void cancel() {
        isBooked = false;
    }

    boolean isAvailable() {
        return !isBooked;
    }
}

public class VenueBookingSystem {
    static Map<String, Venue> venues = new LinkedHashMap<>();

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
        // Initialize venues
        String[] hallNames = {
            "C V Ramanujam Hall", "Radhakrishnan Hall", "Kamarajar Hall",
            "Periyar Hall", "Annadhurai Hall", "Natesa Hall"
        };
        for (String name : hallNames) {
            venues.put(name.toLowerCase(), new Venue(name));
        }

        while (true) {
            System.out.println("\n--- Venue Booking System ---");
            System.out.println("1. View Available Venues");
            System.out.println("2. Book a Venue");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("\nAvailable Venues:");
                    for (Venue v : venues.values()) {
                        if (v.isAvailable()) {
                            System.out.println("- " + v.name);
                        }
                    }
                    break;

                case 2:
                    System.out.print("Enter venue name to book: ");
                    String bookName = sc.nextLine().toLowerCase();
                    Venue bookVenue = venues.get(bookName);
                    if (bookVenue != null && bookVenue.isAvailable()) {
                        bookVenue.book();
                        System.out.println("✅ " + bookVenue.name + " booked successfully.");
                    } else {
                        System.out.println("❌ Venue not available or invalid name.");
                    }
                    break;

                case 3:
                    System.out.print("Enter venue name to cancel booking: ");
                    String cancelName = sc.nextLine().toLowerCase();
                    Venue cancelVenue = venues.get(cancelName);
                    if (cancelVenue != null && !cancelVenue.isAvailable()) {
                        cancelVenue.cancel();
                        System.out.println("✅ Booking for " + cancelVenue.name + " cancelled.");
                    } else {
                        System.out.println("❌ Venue not booked or invalid name.");
                    }
                    break;

                case 4:
                    System.out.println("Exiting system. Goodbye!");
                    return;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
        }
    }
}