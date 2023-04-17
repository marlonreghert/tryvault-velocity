package com.tryvault.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tryvault.serializer.BigDecimalDeserializer;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Represents a LoadFundsRequest model that is used for transferring funds in the application.
 * This class defines the properties and methods for a LoadFundsRequest object, including its ID,
 * customer ID, load amount, and time of the request. It also includes Jackson annotations for
 * custom serialization and deserialization of BigDecimal and ZonedDateTime fields.
 */
public class LoadFundsRequest {
    @JsonProperty("id")
    private long id;
    @JsonProperty("customer_id")
    private long customerId;
    @JsonProperty("load_amount")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal loadAmount;
    @JsonProperty("time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private ZonedDateTime time;

    // Default constructor
    public LoadFundsRequest() {
    }

    // Constructor with parameters
    public LoadFundsRequest(long id, long customerId, BigDecimal loadAmount, ZonedDateTime time) {
        this.id = id;
        this.customerId = customerId;
        this.loadAmount = loadAmount;
        this.time = time;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
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