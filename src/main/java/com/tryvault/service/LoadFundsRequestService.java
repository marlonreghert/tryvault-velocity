package com.tryvault.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import com.tryvault.constants.LoadFundsRequestLimits;
import com.tryvault.entity.LoadFundsRequestEntity;
import com.tryvault.model.LoadFundsRequest;
import com.tryvault.model.LoadFundsResponse;
import com.tryvault.repository.LoadFundsRequestRepository;
import com.tryvault.util.LoadFundsResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class that handles processing of load funds requests. This class performs various checks such as checking if
 * the load request has already been processed, if the customer has exceeded the maximum limits for load requests per day
 * and per week, and creates and saves customer transaction records. It also builds the response object for the load funds
 * request. This class is annotated with {@link org.springframework.stereotype.Service} to indicate that it is a
 * service component in a Spring application context.
 **/

@Service
public class LoadFundsRequestService {

    @Autowired
    private LoadFundsRequestRepository loadFundsRequestRepository;

    private static final Logger LOGGER = LogManager.getLogger(LoadFundsRequestService.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");


    /**
     * Processes a load funds request and returns a load funds response.
     *
     * @param loadFundsRequest The load funds request to be processed.
     * @return The load funds response.
     */
    public LoadFundsResponse processLoadAttempt(LoadFundsRequest loadFundsRequest) {
        long id = loadFundsRequest.getId();
        long customerId = loadFundsRequest.getCustomerId();
        BigDecimal loadAmount = loadFundsRequest.getLoadAmount();
        ZonedDateTime requestTime = loadFundsRequest.getTime();

        LOGGER.info("Handling request (id: {}, customer_id: {})", id, customerId);

        if (isLoadRequestDuplicated(id, customerId)) {
            return null;
        }

        // Response builder
        LoadFundsResponseBuilder loadFundsResponseBuilder = LoadFundsResponseBuilder.fromLoadRequest(loadFundsRequest);
        boolean accepted = true;

        // Check if the customer has exceeded the maximum number of loads per day
        ZonedDateTime startOfDay = requestTime.toLocalDate().atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        if (hasReachedMaximumLoadsDailyCount(startOfDay, endOfDay, customerId)) {
            LOGGER.info("Number of load funds requests reached the maximum limit of {} per day.", LoadFundsRequestLimits.LOADS_PER_DAY);
            accepted = false;
        }

        if (accepted && hasReachedMaximumLoadDailyAmount(startOfDay, endOfDay, customerId, loadAmount)) {
            LOGGER.info("Number of load amount reached it daily limit.");
            accepted = false;
        }

        if (accepted && hasReachedMaximumLoadWeeklyAmount(requestTime, endOfDay, customerId, loadAmount)) {
            LOGGER.info("Number of load funds requests reached it maximum weekly amount.");
            accepted = false;
        }

        // Create and save customer transaction
        LoadFundsRequestEntity loadRequestEntity = new LoadFundsRequestEntity();
        loadRequestEntity.setId(id);
        loadRequestEntity.setCustomerId(customerId);
        loadRequestEntity.setLoadAmount(loadAmount);
        loadRequestEntity.setTime(requestTime);
        loadRequestEntity.setAccepted(accepted);

        LOGGER.info("Writing load funds request to the database.");

        try {
            loadFundsRequestRepository.save(loadRequestEntity);
            LOGGER.info("Request to load funds processed successfully.");
        }
        catch (Exception e) {
            LOGGER.error("The attempt to save the load funds request failed.", e);
            return loadFundsResponseBuilder.build();
        }

        return loadFundsResponseBuilder.accepted(accepted).build();
    }

    private boolean isLoadRequestDuplicated(long id, long customerId) {
        // Check if the load ID has already been processed for the given customer
        return loadFundsRequestRepository.existsByIdAndCustomerId(id, customerId);
    }

    private boolean hasReachedMaximumLoadsDailyCount(ZonedDateTime startOfDay, ZonedDateTime endOfDay, long customerId) {
        LOGGER.info("Looking for requests within date range {} to {}", startOfDay.format(DATE_TIME_FORMATTER), endOfDay.format(DATE_TIME_FORMATTER));

        long numberOfLoadsToday = loadFundsRequestRepository.countByCustomerIdAndTimeBetweenAndAccepted(customerId, startOfDay,
                endOfDay, true);

        return numberOfLoadsToday >= 3;
    }

    private boolean hasReachedMaximumLoadDailyAmount(ZonedDateTime startOfDay, ZonedDateTime endOfDay, long customerId, BigDecimal loadAmount) {
        // Check if the customer has exceeded the maximum amount that can be loaded per day
        BigDecimal totalAmountLoadedToday = loadFundsRequestRepository
                .sumLoadAmountByCustomerIdAndTimeBetween(customerId, startOfDay, endOfDay, true);

        return totalAmountLoadedToday.add(loadAmount).compareTo(LoadFundsRequestLimits.AMOUNT_PER_DAY) >= 0;
    }

    private boolean hasReachedMaximumLoadWeeklyAmount(ZonedDateTime requestTime, ZonedDateTime endOfDay, long customerId, BigDecimal loadAmount) {
        LocalDate startOfWeek = requestTime.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        BigDecimal totalAmountLoadedThisWeek = loadFundsRequestRepository
                .sumLoadAmountByCustomerIdAndTimeBetween(customerId, startOfWeek.atStartOfDay(ZoneOffset.UTC), endOfDay, true);

        return totalAmountLoadedThisWeek.add(loadAmount).compareTo(LoadFundsRequestLimits.AMOUNT_PER_WEEK) >= 0;

    }
}
