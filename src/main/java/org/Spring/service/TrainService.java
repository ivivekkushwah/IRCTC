package org.Spring.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.Spring.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainService {

    private static final String TRAIN_DB_PATH =
            "app/src/main/java/ticket/booking/localDb/trains.json";

    private final ObjectMapper objectMapper;
    private final File trainFile;

    private List<Train> trains;

    /* =========================
       Constructor
       ========================= */

    public TrainService() throws IOException {
        this.objectMapper = createMapper();
        this.trainFile = new File(TRAIN_DB_PATH);
        this.trains = loadTrains();
    }

    /* =========================
       Public APIs
       ========================= */

    public List<Train> searchTrains(String source, String destination) {
        return trains.stream()
                .filter(train -> isValidRoute(train, source, destination))
                .toList();
    }

    public void saveOrUpdateTrain(Train train) throws IOException {
        int index = findTrainIndex(train.getTrainId());

        if (index >= 0) {
            trains.set(index, train);
        } else {
            trains.add(train);
        }

        saveTrains();
    }

    public Optional<Train> findByTrainId(String trainId) {
        return trains.stream()
                .filter(t -> t.getTrainId().equalsIgnoreCase(trainId))
                .findFirst();
    }

    /* =========================
       Persistence
       ========================= */

    private List<Train> loadTrains() throws IOException {
        if (!trainFile.exists()) {
            return new ArrayList<>();
        }

        return objectMapper.readValue(trainFile, new TypeReference<>() {});
    }

    private void saveTrains() throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(trainFile, trains);
    }

    /* =========================
       Validation Logic
       ========================= */

    private boolean isValidRoute(Train train, String source, String destination) {
        List<String> stations = train.getStations();

        int sourceIndex = stations.indexOf(source.toLowerCase());
        int destinationIndex = stations.indexOf(destination.toLowerCase());

        return sourceIndex >= 0
                && destinationIndex >= 0
                && sourceIndex < destinationIndex;
    }

    private int findTrainIndex(String trainId) {
        for (int i = 0; i < trains.size(); i++) {
            if (trains.get(i).getTrainId().equalsIgnoreCase(trainId)) {
                return i;
            }
        }
        return -1;
    }

    /* =========================
       ObjectMapper
       ========================= */

    private ObjectMapper createMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }


}
