import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;

// User class to represent different types of users
class User {
    String username;
    String password;
    String role; // ADMIN, STUDENT
    String fullName;
    String designation; // For admin: Advisor, Assistant Advisor, HOD

    User(String username, String password, String role, String fullName, String designation) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.designation = designation;
    }
}

// Booking Request class
class BookingRequest {
    int requestId;
    String venueName;
    String requesterName;
    String requesterUsername;
    String eventName;
    String eventDescription;
    LocalDate eventDate;
    LocalTime startTime;
    LocalTime endTime;
    String status; // PENDING, APPROVED, REJECTED
    String approvedBy;
    String remarks;

    BookingRequest(int requestId, String venueName, String requesterName, String requesterUsername,
                   String eventName, String eventDescription, LocalDate eventDate, 
                   LocalTime startTime, LocalTime endTime) {
        this.requestId = requestId;
        this.venueName = venueName;
        this.requesterName = requesterName;
        this.requesterUsername = requesterUsername;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "PENDING";
        this.approvedBy = "";
        this.remarks = "";
    }
}

// Venue class with enhanced features
class Venue {
    String name;
    int capacity;
    String facilities;
    List<BookingRequest> approvedBookings;

    Venue(String name, int capacity, String facilities) {
        this.name = name;
        this.capacity = capacity;
        this.facilities = facilities;
        this.approvedBookings = new ArrayList<>();
    }

    boolean isAvailable(LocalDate date, LocalTime startTime, LocalTime endTime) {
        for (BookingRequest booking : approvedBookings) {
            if (booking.eventDate.equals(date)) {
                // Check for time overlap
                if (!(endTime.isBefore(booking.startTime) || startTime.isAfter(booking.endTime))) {
                    return false;
                }
            }
        }
        return true;
    }
}

public class VenueBookingSystem {
    static Map<String, User> users = new HashMap<>();
    static Map<String, Venue> venues = new LinkedHashMap<>();
    static List<BookingRequest> bookingRequests = new ArrayList<>();
    static int requestCounter = 1000;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        initializeSystem();
        
        while (true) {
            displayWelcomeScreen();
            User loggedInUser = login();
            
            if (loggedInUser != null) {
                if (loggedInUser.role.equals("ADMIN")) {
                    adminDashboard(loggedInUser);
                } else {
                    studentDashboard(loggedInUser);
                }
            }
        }
    }

    static void initializeSystem() {
        // Initialize admin users
        users.put("advisor", new User("advisor", "adv123", "ADMIN", "Dr. Kumar Sharma", "Advisor"));
        users.put("asstadv", new User("asstadv", "asst123", "ADMIN", "Prof. Priya Singh", "Assistant Advisor"));
        users.put("hod", new User("hod", "hod123", "ADMIN", "Dr. Rajesh Verma", "Head of Department"));
        
        // Initialize student/coordinator users
        users.put("student1", new User("student1", "stu123", "STUDENT", "Amit Kumar", "Student Coordinator"));
        users.put("student2", new User("student2", "stu456", "STUDENT", "Neha Patel", "Event Coordinator"));
        
        // Initialize venues with capacity and facilities
        venues.put("c v ramanujam hall", new Venue("C V Ramanujam Hall", 500, "Projector, AC, Sound System"));
        venues.put("radhakrishnan hall", new Venue("Radhakrishnan Hall", 300, "Projector, AC"));
        venues.put("kamarajar hall", new Venue("Kamarajar Hall", 400, "Projector, AC, Stage"));
        venues.put("periyar hall", new Venue("Periyar Hall", 250, "Projector, Whiteboard"));
        venues.put("annadhurai hall", new Venue("Annadhurai Hall", 350, "AC, Sound System, Stage"));
        venues.put("natesa hall", new Venue("Natesa Hall", 200, "Projector, AC"));
    }

    static void displayWelcomeScreen() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║          🏛️  VENUE BOOKING MANAGEMENT SYSTEM 🏛️           ║");
        System.out.println("║                                                            ║");
        System.out.println("║              Welcome to Hall Booking Portal                ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    static User login() {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│          LOGIN TO CONTINUE             │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.println();
        
        System.out.println("📝 Demo Credentials:");
        System.out.println("   Admin: advisor/adv123, asstadv/asst123, hod/hod123");
        System.out.println("   Student: student1/stu123, student2/stu456");
        System.out.println();
        
        System.out.print("👤 Username: ");
        String username = sc.nextLine().trim();
        System.out.print("🔒 Password: ");
        String password = sc.nextLine().trim();

        User user = users.get(username);
        if (user != null && user.password.equals(password)) {
            System.out.println("\n✅ Login Successful!");
            System.out.println("Welcome, " + user.fullName + " (" + user.designation + ")");
            pause();
            return user;
        } else {
            System.out.println("\n❌ Invalid credentials! Please try again.");
            pause();
            return null;
        }
    }

    static void adminDashboard(User admin) {
        while (true) {
            clearScreen();
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║              ADMIN DASHBOARD - " + admin.designation);
            System.out.println("║              Logged in as: " + admin.fullName);
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("┌────────────────────────────────────────┐");
            System.out.println("│         ADMIN MENU OPTIONS             │");
            System.out.println("├────────────────────────────────────────┤");
            System.out.println("│  1. 📋 View Pending Booking Requests   │");
            System.out.println("│  2. ✅ Approve Booking Request          │");
            System.out.println("│  3. ❌ Reject Booking Request           │");
            System.out.println("│  4. 📊 View All Bookings               │");
            System.out.println("│  5. 🏛️  View All Venues                 │");
            System.out.println("│  6. 📅 View Venue Schedule             │");
            System.out.println("│  7. 🚪 Logout                          │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.println();
            System.out.print("Enter your choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    viewPendingRequests();
                    break;
                case 2:
                    approveBookingRequest(admin);
                    break;
                case 3:
                    rejectBookingRequest(admin);
                    break;
                case 4:
                    viewAllBookings();
                    break;
                case 5:
                    viewAllVenues();
                    break;
                case 6:
                    viewVenueSchedule();
                    break;
                case 7:
                    System.out.println("\n🚪 Logging out...");
                    pause();
                    return;
                default:
                    System.out.println("\n❌ Invalid option! Try again.");
                    pause();
            }
        }
    }

    static void studentDashboard(User student) {
        while (true) {
            clearScreen();
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║           STUDENT/COORDINATOR DASHBOARD                    ║");
            System.out.println("║           Logged in as: " + student.fullName);
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("┌────────────────────────────────────────┐");
            System.out.println("│         STUDENT MENU OPTIONS           │");
            System.out.println("├────────────────────────────────────────┤");
            System.out.println("│  1. 🏛️  View Available Venues           │");
            System.out.println("│  2. 📝 Submit Booking Request          │");
            System.out.println("│  3. 📊 View My Booking Requests        │");
            System.out.println("│  4. 📅 Check Venue Availability        │");
            System.out.println("│  5. 🚪 Logout                          │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.println();
            System.out.print("Enter your choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    viewAllVenues();
                    break;
                case 2:
                    submitBookingRequest(student);
                    break;
                case 3:
                    viewMyBookingRequests(student);
                    break;
                case 4:
                    checkVenueAvailability();
                    break;
                case 5:
                    System.out.println("\n🚪 Logging out...");
                    pause();
                    return;
                default:
                    System.out.println("\n❌ Invalid option! Try again.");
                    pause();
            }
        }
    }

    static void viewAllVenues() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    AVAILABLE VENUES                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        int count = 1;
        for (Venue venue : venues.values()) {
            System.out.println("┌────────────────────────────────────────────────────────────┐");
            System.out.println("│ " + count + ". " + venue.name);
            System.out.println("│    Capacity: " + venue.capacity + " persons");
            System.out.println("│    Facilities: " + venue.facilities);
            System.out.println("│    Total Approved Bookings: " + venue.approvedBookings.size());
            System.out.println("└────────────────────────────────────────────────────────────┘");
            count++;
        }
        pause();
    }

    static void submitBookingRequest(User student) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              SUBMIT NEW BOOKING REQUEST                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Display venues
        int count = 1;
        List<String> venueList = new ArrayList<>(venues.keySet());
        for (String venueName : venueList) {
            System.out.println(count + ". " + venues.get(venueName).name);
            count++;
        }
        System.out.println();
        System.out.print("Select venue number: ");
        int venueChoice = getIntInput();
        
        if (venueChoice < 1 || venueChoice > venueList.size()) {
            System.out.println("❌ Invalid venue selection!");
            pause();
            return;
        }

        String selectedVenueKey = venueList.get(venueChoice - 1);
        Venue selectedVenue = venues.get(selectedVenueKey);

        System.out.print("\n📝 Event Name: ");
        String eventName = sc.nextLine();
        
        System.out.print("📝 Event Description: ");
        String eventDescription = sc.nextLine();
        
        System.out.print("📅 Event Date (YYYY-MM-DD): ");
        String dateStr = sc.nextLine();
        LocalDate eventDate;
        try {
            eventDate = LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("❌ Invalid date format!");
            pause();
            return;
        }

        System.out.print("🕐 Start Time (HH:MM, 24-hour format): ");
        String startTimeStr = sc.nextLine();
        LocalTime startTime;
        try {
            startTime = LocalTime.parse(startTimeStr);
        } catch (Exception e) {
            System.out.println("❌ Invalid time format!");
            pause();
            return;
        }

        System.out.print("🕐 End Time (HH:MM, 24-hour format): ");
        String endTimeStr = sc.nextLine();
        LocalTime endTime;
        try {
            endTime = LocalTime.parse(endTimeStr);
        } catch (Exception e) {
            System.out.println("❌ Invalid time format!");
            pause();
            return;
        }

        // Check availability
        if (!selectedVenue.isAvailable(eventDate, startTime, endTime)) {
            System.out.println("\n❌ Sorry! The venue is not available for the selected date and time.");
            System.out.println("   Please check the venue schedule and try a different time slot.");
            pause();
            return;
        }

        BookingRequest request = new BookingRequest(
            requestCounter++, selectedVenue.name, student.fullName, student.username,
            eventName, eventDescription, eventDate, startTime, endTime
        );
        
        bookingRequests.add(request);
        
        System.out.println("\n✅ Booking request submitted successfully!");
        System.out.println("   Request ID: #" + request.requestId);
        System.out.println("   Status: Pending Approval");
        System.out.println("\n   Your request will be reviewed by the authorities.");
        pause();
    }

    static void viewMyBookingRequests(User student) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                 MY BOOKING REQUESTS                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        boolean found = false;
        for (BookingRequest req : bookingRequests) {
            if (req.requesterUsername.equals(student.username)) {
                found = true;
                displayBookingRequest(req);
            }
        }

        if (!found) {
            System.out.println("📭 No booking requests found.");
        }
        pause();
    }

    static void viewPendingRequests() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              PENDING BOOKING REQUESTS                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        boolean found = false;
        for (BookingRequest req : bookingRequests) {
            if (req.status.equals("PENDING")) {
                found = true;
                displayBookingRequest(req);
            }
        }

        if (!found) {
            System.out.println("📭 No pending requests.");
        }
        pause();
    }

    static void approveBookingRequest(User admin) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              APPROVE BOOKING REQUEST                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        System.out.print("Enter Request ID to approve: #");
        int requestId = getIntInput();

        BookingRequest request = findRequestById(requestId);
        if (request == null) {
            System.out.println("❌ Request not found!");
            pause();
            return;
        }

        if (!request.status.equals("PENDING")) {
            System.out.println("❌ This request has already been " + request.status.toLowerCase() + "!");
            pause();
            return;
        }

        System.out.print("Add remarks (optional): ");
        String remarks = sc.nextLine();

        request.status = "APPROVED";
        request.approvedBy = admin.fullName + " (" + admin.designation + ")";
        request.remarks = remarks;

        // Add to venue's approved bookings
        Venue venue = venues.get(request.venueName.toLowerCase());
        if (venue != null) {
            venue.approvedBookings.add(request);
        }

        System.out.println("\n✅ Booking request #" + requestId + " has been APPROVED!");
        System.out.println("   Venue: " + request.venueName);
        System.out.println("   Event: " + request.eventName);
        System.out.println("   Requester will be notified.");
        pause();
    }

    static void rejectBookingRequest(User admin) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              REJECT BOOKING REQUEST                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        System.out.print("Enter Request ID to reject: #");
        int requestId = getIntInput();

        BookingRequest request = findRequestById(requestId);
        if (request == null) {
            System.out.println("❌ Request not found!");
            pause();
            return;
        }

        if (!request.status.equals("PENDING")) {
            System.out.println("❌ This request has already been " + request.status.toLowerCase() + "!");
            pause();
            return;
        }

        System.out.print("Reason for rejection: ");
        String remarks = sc.nextLine();

        request.status = "REJECTED";
        request.approvedBy = admin.fullName + " (" + admin.designation + ")";
        request.remarks = remarks;

        System.out.println("\n❌ Booking request #" + requestId + " has been REJECTED!");
        System.out.println("   Reason: " + remarks);
        pause();
    }

    static void viewAllBookings() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   ALL BOOKING REQUESTS                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        if (bookingRequests.isEmpty()) {
            System.out.println("📭 No booking requests found.");
        } else {
            for (BookingRequest req : bookingRequests) {
                displayBookingRequest(req);
            }
        }
        pause();
    }

    static void checkVenueAvailability() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              CHECK VENUE AVAILABILITY                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        System.out.print("📅 Enter Date (YYYY-MM-DD): ");
        String dateStr = sc.nextLine();
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("❌ Invalid date format!");
            pause();
            return;
        }

        System.out.println("\n🏛️  Venue Availability on " + date + ":\n");
        
        for (Venue venue : venues.values()) {
            System.out.println("┌────────────────────────────────────────────────────────────┐");
            System.out.println("│ " + venue.name);
            
            boolean hasBookings = false;
            for (BookingRequest booking : venue.approvedBookings) {
                if (booking.eventDate.equals(date)) {
                    hasBookings = true;
                    System.out.println("│   ⏰ " + booking.startTime + " - " + booking.endTime + 
                                     " | " + booking.eventName);
                }
            }
            
            if (!hasBookings) {
                System.out.println("│   ✅ Available all day");
            }
            System.out.println("└────────────────────────────────────────────────────────────┘");
        }
        pause();
    }

    static void viewVenueSchedule() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   VENUE SCHEDULE                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        int count = 1;
        List<String> venueList = new ArrayList<>(venues.keySet());
        for (String venueName : venueList) {
            System.out.println(count + ". " + venues.get(venueName).name);
            count++;
        }
        System.out.println();
        System.out.print("Select venue number: ");
        int venueChoice = getIntInput();
        
        if (venueChoice < 1 || venueChoice > venueList.size()) {
            System.out.println("❌ Invalid selection!");
            pause();
            return;
        }

        Venue selectedVenue = venues.get(venueList.get(venueChoice - 1));
        
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         SCHEDULE FOR " + selectedVenue.name);
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        if (selectedVenue.approvedBookings.isEmpty()) {
            System.out.println("📭 No approved bookings for this venue.");
        } else {
            for (BookingRequest booking : selectedVenue.approvedBookings) {
                System.out.println("┌────────────────────────────────────────────────────────────┐");
                System.out.println("│ Event: " + booking.eventName);
                System.out.println("│ Date: " + booking.eventDate);
                System.out.println("│ Time: " + booking.startTime + " - " + booking.endTime);
                System.out.println("│ Organizer: " + booking.requesterName);
                System.out.println("└────────────────────────────────────────────────────────────┘");
            }
        }
        pause();
    }

    static void displayBookingRequest(BookingRequest req) {
        String statusIcon = req.status.equals("APPROVED") ? "✅" : 
                           req.status.equals("REJECTED") ? "❌" : "⏳";
        
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│ Request ID: #" + req.requestId + " | Status: " + statusIcon + " " + req.status);
        System.out.println("│ ───────────────────────────────────────────────────────────");
        System.out.println("│ Venue: " + req.venueName);
        System.out.println("│ Event: " + req.eventName);
        System.out.println("│ Description: " + req.eventDescription);
        System.out.println("│ Date: " + req.eventDate);
        System.out.println("│ Time: " + req.startTime + " - " + req.endTime);
        System.out.println("│ Requested by: " + req.requesterName);
        if (!req.approvedBy.isEmpty()) {
            System.out.println("│ Reviewed by: " + req.approvedBy);
        }
        if (!req.remarks.isEmpty()) {
            System.out.println("│ Remarks: " + req.remarks);
        }
        System.out.println("└────────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    static BookingRequest findRequestById(int requestId) {
        for (BookingRequest req : bookingRequests) {
            if (req.requestId == requestId) {
                return req;
            }
        }
        return null;
    }

    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pause() {
        System.out.println("\nPress Enter to continue...");
        sc.nextLine();
    }

    static int getIntInput() {
        while (true) {
            try {
                int value = Integer.parseInt(sc.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input! Enter a number: ");
            }
        }
    }
}