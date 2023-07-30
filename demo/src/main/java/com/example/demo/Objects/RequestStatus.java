package com.example.demo.Objects;

public enum RequestStatus {
    ACCEPTED("STATUS_ACCEPTED"),
    DECLINED("STATUS_DECLINED"),
    PENDING("STATUS_PENDING");

    private String status;

    private RequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }


}
