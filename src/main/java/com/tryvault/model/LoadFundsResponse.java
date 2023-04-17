package com.tryvault.model;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LoadFundsResponse is a model class that represents the response object for a load funds request.
 * It contains the ID, customer ID, and acceptance status of the load funds request.
 */
public class LoadFundsResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("customer_id")
    private String customerId;
    @JsonProperty("accepted")
    private boolean accepted;

    // Default constructor
    public LoadFundsResponse() {
    }

    // Constructor with parameters
    public LoadFundsResponse(String id, String customerId, boolean accepted) {
        this.id = id;
        this.customerId = customerId;
        this.accepted = accepted;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}