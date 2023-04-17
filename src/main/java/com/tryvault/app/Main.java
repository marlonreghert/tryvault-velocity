package com.tryvault.app;

import com.tryvault.model.LoadFundsRequest;
import com.tryvault.repository.LoadFundsRequestRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.tryvault.io.LoadFundsRequestReader;
import com.tryvault.io.LoadFundsResponseWriter;
import com.tryvault.service.LoadFundsRequestService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EntityScan(basePackages = "com.tryvault.entity")
@EnableJpaRepositories("com.tryvault.repository")
@ComponentScan(basePackages = {"com.tryvault"})
@SpringBootApplication
public class Main {

    private LoadFundsRequestService loadFundsRequestService; // Injecting LoadService bean

    private LoadFundsRequestReader loadFundsRequestReader; // Injecting LoadService bean

    private LoadFundsResponseWriter loadFundsResponseWriter; // Injecting LoadService bean

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    @Autowired
    public Main(LoadFundsRequestService loadFundsRequestService,
                LoadFundsRequestReader loadFundsRequestReader,
                LoadFundsResponseWriter loadFundsResponseWriter) {
        this.loadFundsRequestService = loadFundsRequestService;
        this.loadFundsRequestReader = loadFundsRequestReader;
        this.loadFundsResponseWriter = loadFundsResponseWriter;
    }

    public static void main(String[] args) {
        try {
            // Start Spring app
            ApplicationContext context = SpringApplication.run(Main.class, args);

            LoadFundsRequestService loadFundsRequestService = context.getBean(LoadFundsRequestService.class);
            LoadFundsRequestReader loadFundsRequestReader = context.getBean(LoadFundsRequestReader.class);
            LoadFundsResponseWriter loadFundsResponseWriter = context.getBean(LoadFundsResponseWriter.class);

            // Get the path of the file containing the load attempts
            if (args.length != 2) {
                LOGGER.error("Usage: java com.tryvault.app.Main <filePath> <outputPath>");
                System.exit(1);
            }

            // Read arg
            String loadAttemptsFilePath = args[0];
            String outputFilePath = args[1];

            // Read the client's loads attempts
            LOGGER.info("Reading attempts at entry path: {}.", loadAttemptsFilePath);

            List<LoadFundsRequest> loadFundsRequests = loadFundsRequestReader.readFromFile(loadAttemptsFilePath);
            // Process LoadAttempt using LoadService
            LOGGER.info("Processing load funds requests.");
            LOGGER.info("loadService: ", loadFundsRequestService);

            loadFundsResponseWriter.writeToFile(loadFundsRequests.stream()
                    .map(loadFundsRequestService::processLoadAttempt)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()), outputFilePath);

            LOGGER.info("Exiting app after processing {} load funds requests", loadFundsRequests.size());
        } catch (Exception exception) {
            LOGGER.info("An exception was thrown while processing the load attempts", exception);
        }
    }
}
