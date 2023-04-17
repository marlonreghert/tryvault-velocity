package com.tryvault.util;

import com.tryvault.model.LoadFundsRequest;
import com.tryvault.model.LoadFundsResponse;
import org.springframework.stereotype.Component;


/**
 * Utility class that builds {@link com.tryvault.model.LoadFundsResponse} objects from {@link
 * com.tryvault.model.LoadFundsRequest} objects. This class provides a fluent builder pattern to set properties such as
 * "accepted" on the response object. It is annotated with {@link org.springframework.stereotype.Component} to indicate
 * that it is a Spring component that can be automatically detected and instantiated by the Spring container.
 *
 * <p>
 * Example usage:
 * <pre>
 * LoadFundsResponseBuilder.loadFundsResponse = LoadFundsResponseBuilder.fromLoadRequest(loadFundsRequest)
 *                            .accepted(true)
 *                            .build();
 * </pre>
 */
@Component
public class LoadFundsResponseBuilder {

    private long id;
    private long customerId;
    private boolean accepted;

    public static LoadFundsResponseBuilder fromLoadRequest(LoadFundsRequest loadFundsRequest) {
        LoadFundsResponseBuilder loadFundsResponse = new LoadFundsResponseBuilder();
        loadFundsResponse.id = loadFundsRequest.getId();
        loadFundsResponse.customerId = loadFundsRequest.getCustomerId();

        return loadFundsResponse;
    }

    public LoadFundsResponseBuilder accepted(boolean accepted) {
        this.accepted = accepted;
        return this;
    }

    public LoadFundsResponse build() {
        LoadFundsResponse loadFundsResponse = new LoadFundsResponse();
        loadFundsResponse.setId(String.valueOf(id));
        loadFundsResponse.setCustomerId(String.valueOf(customerId));
        loadFundsResponse.setAccepted(accepted);

        return loadFundsResponse;
    }
}

