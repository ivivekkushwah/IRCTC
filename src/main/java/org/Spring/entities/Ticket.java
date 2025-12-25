package org.Spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Ticket {

    private String ticketId;
    private String userId;
    private String source;
    private String destination;
    private LocalDate dateOfTravel;
    private Train train;

    // Required by Jackson
    public Ticket() {
    }

    private Ticket(
            String ticketId,
            String userId,
            String source,
            String destination,
            LocalDate dateOfTravel,
            Train train
    ) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
    }

    /* =========================
       Factory Method (IMPORTANT)
       ========================= */

    public static Ticket create(
            User user,
            Train train,
            String source,
            String destination,
            LocalDate dateOfTravel
    ) {
        return new Ticket(
                UUID.randomUUID().toString(),
                user.getUserId(),
                source,
                destination,
                dateOfTravel,
                train
        );
    }

    /* =========================
       Business Method
       ========================= */

    public String getTicketInfo() {
        return String.format(
                "Ticket ID: %s | User: %s | %s → %s | Date: %s | Train: %s",
                ticketId,
                userId,
                source,
                destination,
                dateOfTravel,
                train.getTrainId()
        );
    }

    /* =========================
       Getters (NO setters)
       ========================= */

    public String getTicketId() {
        return ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDateOfTravel() {
        return dateOfTravel;
    }

    public Train getTrain() {
        return train;
    }
}
