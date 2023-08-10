package com.example.demo.Objects;

/**
 * Enum representing status of request
 *
 * @author Thorvas
 */
public enum RequestStatus {
    ACCEPTED("STATUS_ACCEPTED"),
    DECLINED("STATUS_DECLINED"),
    PENDING("STATUS_PENDING");

    private final String status;

    RequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }


}
