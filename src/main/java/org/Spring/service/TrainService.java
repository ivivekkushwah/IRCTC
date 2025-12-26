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
            "src/main/java/org/Spring/localdb/train.json";

    private final ObjectMapper objectMapper;
    private final File trainFile;
    private final List<Train> trains;

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

        // 🔥 NORMALIZE INPUT ONCE
        source = source.trim().toLowerCase();
        destination = destination.trim().toLowerCase();

        List<Train> result = new ArrayList<>();

        for (Train train : trains) {

            // 🔎 DEBUG (keep while testing)
            System.out.println("Checking train " + train.getTrainNo());
            System.out.println("Stations: " + train.getStations());

            if (isValidRoute(train, source, destination)) {
                result.add(train);
            }
        }

        return result;
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
       Validation Logic (FIXED)
       ========================= */

    private boolean isValidRoute(Train train, String source, String destination) {

        List<String> stations = train.getStations()
                .stream()
                .map(String::toLowerCase)
                .toList();

        int sourceIndex = stations.indexOf(source);
        int destinationIndex = stations.indexOf(destination);

        System.out.println("srcIndex=" + sourceIndex + ", destIndex=" + destinationIndex);

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

