package com.tryvault.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryvault.app.Main;
import com.tryvault.model.LoadFundsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


/**
 LoadFundsResponseWriter is a component used to write LoadFundsResponse objects to different destinations, such as standard output or a file, in JSON format.
 It uses the Jackson library for JSON serialization.
 */
@Component
public class LoadFundsResponseWriter {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    /**
     * Writes a LoadFundsResponse object to standard output in JSON format.
     * @param loadFundsResponse The LoadFundsResponse object to be written.
     */
    public void writeToStandardOutput(LoadFundsResponse loadFundsResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(loadFundsResponse);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to write LoadResponse object as JSON to standard output,", e);
        }
    }

    /**
     * Writes a list of LoadFundsResponse objects to a file in JSON format.
     * @param loadFundsResponses The list of LoadFundsResponse objects to be written.
     * @param outputPath The path of the file to write the JSON data to.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void writeToFile(List<LoadFundsResponse> loadFundsResponses, String outputPath) throws IOException {
        // Create an ObjectMapper to convert objects to JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Create a BufferedWriter to write to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // Iterate over the list of responses

            boolean shouldBreakLine = false;
            for (LoadFundsResponse response : loadFundsResponses) {
                // Convert the response object to JSON
                if (shouldBreakLine) {
                    writer.newLine();
                }
                shouldBreakLine = true;
                String json = objectMapper.writeValueAsString(response);
                // Write the JSON to the file, followed by a newline
                writer.write(json);
            }
        }
    }
}
