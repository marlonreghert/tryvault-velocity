package com.tryvault.repository;

import com.tryvault.entity.LoadFundsRequestEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * LoadFundsRequestRepository is a Spring Data JPA repository interface that provides
 * data access methods for managing load funds requests in the database.
 **/
@Repository
public interface LoadFundsRequestRepository extends JpaRepository<LoadFundsRequestEntity, Long> {

    /**
     * Checks if a load funds request with the given ID and customer ID exists in the database.
     *
     * @param id         The ID of the load funds request.
     * @param customerId The customer ID associated with the load funds request.
     * @return True if a load funds request with the given ID and customer ID exists, false otherwise.
     */
    boolean existsByIdAndCustomerId(long id, long customerId);

    /**
     * Counts the number of load funds requests for a given customer ID and within a specified time range.
     *
     * @param customerId The customer ID associated with the load funds requests.
     * @param startTime  The start time of the time range.
     * @param endTime    The end time of the time range.
     * @param accepted   If the transaction was accepted or not
     * @return The count of load funds requests within the specified time range.
     */
    long countByCustomerIdAndTimeBetweenAndAccepted(long customerId, ZonedDateTime startTime, ZonedDateTime endTime, boolean accepted);

    /**
     * Retrieves the sum of load amounts for load funds requests of a given customer ID and within a specified time range.
     *
     * @param customerId The customer ID associated with the load funds requests.
     * @param startTime  The start time of the time range.
     * @param endTime    The end time of the time range.
     * @param accepted   If the transaction was accepted or not
     * @return The sum of load amounts within the specified time range.
     */
    @Query("SELECT COALESCE(SUM(t.loadAmount), 0) FROM LoadFundsRequestEntity t WHERE t.accepted = ?4 AND t.customerId = ?1 AND t.time BETWEEN ?2 AND ?3")
    BigDecimal sumLoadAmountByCustomerIdAndTimeBetween(long customerId, ZonedDateTime startTime, ZonedDateTime endTime, boolean accepted);
}