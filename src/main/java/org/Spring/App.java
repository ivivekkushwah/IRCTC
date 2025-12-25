package org.Spring;

import org.Spring.entities.Ticket;
import org.Spring.entities.Train;
import org.Spring.entities.User;
import org.Spring.service.UserBookingService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {

        System.out.println("🚆 Train Booking System");
        Scanner scanner = new Scanner(System.in);

        UserBookingService userBookingService;
        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            System.out.println("❌ Failed to start application");
            return;
        }

        Train selectedTrain = null;
        int option;

        while (true) {
            System.out.println("""
                    
                    Choose an option:
                    1. Sign up
                    2. Login
                    3. Fetch Bookings
                    4. Search Trains
                    5. Book a Seat
                    6. Cancel Booking
                    7. Exit
                    """);

            option = scanner.nextInt();

            try {
                switch (option) {

                    case 1 -> {
                        System.out.print("Username: ");
                        String username = scanner.next();
                        System.out.print("Password: ");
                        String password = scanner.next();

                        User user = new User(
                                username,
                                password,
                                null,
                                null,
                                UUID.randomUUID().toString()
                        );

                        boolean signedUp = userBookingService.signUp(user);
                        System.out.println(
                                signedUp ? "✅ Signup successful" : "❌ Username already exists"
                        );
                    }

                    case 2 -> {
                        System.out.print("Username: ");
                        String username = scanner.next();
                        System.out.print("Password: ");
                        String password = scanner.next();

                        User loginUser = new User(
                                username,
                                password,
                                null,
                                null,
                                null
                        );

                        boolean loggedIn = userBookingService.login(loginUser);
                        System.out.println(
                                loggedIn ? "✅ Login successful" : "❌ Invalid credentials"
                        );
                    }

                    case 3 -> {
                        List<Ticket> tickets = userBookingService.fetchBookings();
                        if (tickets.isEmpty()) {
                            System.out.println("No bookings found");
                        } else {
                            tickets.forEach(t -> System.out.println(t.getTicketInfo()));
                        }
                    }

                    case 4 -> {
                        System.out.print("Source: ");
                        String source = scanner.next();
                        System.out.print("Destination: ");
                        String destination = scanner.next();

                        List<Train> trains =
                                userBookingService.searchTrains(source, destination);

                        if (trains.isEmpty()) {
                            System.out.println("No trains found");
                            break;
                        }

                        for (int i = 0; i < trains.size(); i++) {
                            Train t = trains.get(i);
                            System.out.println((i + 1) + ". Train ID: " + t.getTrainId());
                            for (Map.Entry<String, String> e : t.getStationTimes().entrySet()) {
                                System.out.println("   " + e.getKey() + " -> " + e.getValue());
                            }
                        }

                        System.out.print("Select train (1-" + trains.size() + "): ");
                        selectedTrain = trains.get(scanner.nextInt() - 1);
                    }

                    case 5 -> {
                        if (selectedTrain == null) {
                            System.out.println("❌ Please search and select a train first");
                            break;
                        }

                        List<List<Integer>> seats = selectedTrain.getSeats();
                        for (List<Integer> row : seats) {
                            row.forEach(s -> System.out.print(s + " "));
                            System.out.println();
                        }

                        System.out.print("Row: ");
                        int row = scanner.nextInt();
                        System.out.print("Seat: ");
                        int seat = scanner.nextInt();

                        System.out.print("Travel date (YYYY-MM-DD): ");
                        LocalDate date = LocalDate.parse(scanner.next());

                        boolean booked = userBookingService.bookTrainSeat(
                                selectedTrain,
                                row,
                                seat,
                                selectedTrain.getStations().get(0),
                                selectedTrain.getStations().get(selectedTrain.getStations().size() - 1),
                                date
                        );

                        System.out.println(
                                booked ? "✅ Seat booked successfully" : "❌ Seat not available"
                        );
                    }

                    case 6 -> {
                        System.out.print("Enter Ticket ID to cancel: ");
                        String ticketId = scanner.next();
                        boolean cancelled = userBookingService.cancelBooking(ticketId);
                        System.out.println(
                                cancelled ? "✅ Ticket cancelled" : "❌ Ticket not found"
                        );
                    }

                    case 7 -> {
                        System.out.println("👋 Goodbye!");
                        return;
                    }

                    default -> System.out.println("❌ Invalid option");
                }

            } catch (Exception ex) {
                System.out.println("❌ Error: " + ex.getMessage());
            }
        }
    }
}
