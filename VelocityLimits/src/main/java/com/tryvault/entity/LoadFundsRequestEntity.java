package com.tryvault.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Represents a LoadFundsRequest entity in the database.
 * This class is used to map and persist LoadFundsRequest objects to the "load_funds_request" table
 * in the "public" schema of the database using JPA annotations.
 */
@Entity
@Table(name = "load_funds_request", schema = "public")
public class LoadFundsRequestEntity {

    @Id
    private Long id;

    private long customerId;

    private BigDecimal loadAmount;

    private ZonedDateTime time;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    private boolean accepted;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getLoadAmount() {
        return loadAmount;
    }

    public void setLoadAmount(BigDecimal loadAmount) {
        this.loadAmount = loadAmount;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }
}
