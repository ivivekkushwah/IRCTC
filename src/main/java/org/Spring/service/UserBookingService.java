package org.Spring.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.Spring.entities.Ticket;
import org.Spring.entities.Train;
import org.Spring.entities.User;
import org.Spring.utils.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class UserBookingService {

    private static final String USER_FILE_PATH =
            "app/src/main/java/ticket/booking/localDb/users.json";

    private final ObjectMapper objectMapper;
    private final File userFile;

    private final List<User> users;
    private User currentUser;

    /* =========================
       Constructors
       ========================= */

    public UserBookingService() throws IOException {
        this.objectMapper = createMapper();
        this.userFile = new File(USER_FILE_PATH);
        this.users = loadUsers();
    }

    public UserBookingService(User user) throws IOException {
        this();
        this.currentUser = authenticate(user)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    }

    /* =========================
       Authentication
       ========================= */

    public boolean login(User loginRequest) {
        Optional<User> user = authenticate(loginRequest);
        user.ifPresent(u -> this.currentUser = u);
        return user.isPresent();
    }

    public boolean signUp(User newUser) throws IOException {
        boolean exists = users.stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(newUser.getName()));

        if (exists) return false;

        newUser.setHashedPassword(
                UserServiceUtil.hashPassword(newUser.getPassword())
        );
        newUser.setPassword(null);
        newUser.setTicketsBooked(new ArrayList<>());

        users.add(newUser);
        saveUsers();
        return true;
    }

    private Optional<User> authenticate(User loginRequest) {
        return users.stream()
                .filter(u ->
                        u.getName().equalsIgnoreCase(loginRequest.getName()) &&
                                UserServiceUtil.checkPassword(
                                        loginRequest.getPassword(),
                                        u.getHashedPassword()
                                )
                )
                .findFirst();
    }

    /* =========================
       Booking Operations
       ========================= */

    public List<Ticket> fetchBookings() {
        ensureLoggedIn();
        return List.copyOf(currentUser.getTicketsBooked());
    }

    public boolean cancelBooking(String ticketId) throws IOException {
        ensureLoggedIn();

        if (ticketId == null || ticketId.isBlank()) return false;

        boolean removed = currentUser.getTicketsBooked()
                .removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        if (removed) {
            saveUsers();
        }

        return removed;
    }

    /* =========================
       Train Operations
       ========================= */

    public List<Train> searchTrains(String source, String destination) throws IOException {
        TrainService trainService = new TrainService();
        return trainService.searchTrains(source, destination);
    }

    public boolean bookTrainSeat(
            Train train,
            int row,
            int seat,
            String source,
            String destination,
            LocalDate dateOfTravel
    ) throws IOException {

        ensureLoggedIn();

        List<List<Integer>> seats = train.getSeats();

        if (!isSeatValid(seats, row, seat)) {
            return false;
        }

        // Mark seat as booked
        seats.get(row).set(seat, 1);
        train.setSeats(seats);

        // Persist train update
        TrainService trainService = new TrainService();
        trainService.saveOrUpdateTrain(train);

        // ✅ CORRECT Ticket creation
        Ticket ticket = Ticket.create(
                currentUser,
                train,
                source,
                destination,
                dateOfTravel
        );

        currentUser.getTicketsBooked().add(ticket);
        saveUsers();

        return true;
    }

    private boolean isSeatValid(List<List<Integer>> seats, int row, int seat) {
        return row >= 0 && row < seats.size()
                && seat >= 0 && seat < seats.get(row).size()
                && seats.get(row).get(seat) == 0;
    }

    /* =========================
       Persistence
       ========================= */

    private List<User> loadUsers() throws IOException {
        if (!userFile.exists()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(userFile, new TypeReference<>() {});
    }

    private void saveUsers() throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(userFile, users);
    }

    private ObjectMapper createMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    /* =========================
       Utility
       ========================= */

    private void ensureLoggedIn() {
        if (currentUser == null) {
            throw new IllegalStateException("User not logged in");
        }
    }
}
