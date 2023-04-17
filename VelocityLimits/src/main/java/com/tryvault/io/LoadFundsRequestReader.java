package com.tryvault.io;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tryvault.model.LoadFundsRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 LoadFundsRequestReader is a component used to read LoadFundsRequest objects from a file in JSON format.
 It uses the Jackson library for JSON deserialization and provides methods to read LoadFundsRequest objects from a file
 and map them to Java objects. This class also handles exceptions that may occur during the deserialization process.
 LoadFundsRequestReader is a Spring component, annotated with @Component, allowing it to be used as a bean in a Spring application context.
 It contains a method to read LoadFundsRequest objects from a file, making it useful for retrieving request data from external sources.
 */
@Component
public class LoadFundsRequestReader {

    private static final Logger LOGGER = LogManager.getLogger(LoadFundsRequestReader.class);

    private ObjectMapper objectMapper;

    public LoadFundsRequestReader(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    public LoadFundsRequestReader() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
    }


    /**
     * Reads LoadFundsRequest objects from a file in JSON format.
     * This method takes a file path as input and reads the file line by line.
     * Each line is parsed as a separate JSON object and mapped to a LoadFundsRequest object.
     * The resulting LoadFundsRequest objects are added to a list and returned.
     *
     * @param filePath The file path of the file to be read.
     * @return A list of LoadFundsRequest objects read from the file.
     * @throws IOException If an I/O error occurs during the file reading process.
     */
    public List<LoadFundsRequest> readFromFile(String filePath) throws IOException {
        LOGGER.info("Reading entries from filePath {}", filePath);

        // Create ObjectMapper instance
        List<LoadFundsRequest> loadFundsRequests = new ArrayList<>();

        LOGGER.info("Reading lines");

        // Read file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line as a separate JSON object and map it to LoadFundsRequest
                LoadFundsRequest loadFundsRequest = objectMapper.readValue(line, LoadFundsRequest.class);
                loadFundsRequests.add(loadFundsRequest);
            }
        }

        return loadFundsRequests;
    }
}
