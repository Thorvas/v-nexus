package com.example.demo.Project;


public enum ProjectStatus {

    STATUS_OPEN("STATUS_OPEN"),
    STATUS_CLOSED("STATUS_CLOSED"),
    STATUS_FINISHED("STATUS_FINISHED");

    public String status;

    ProjectStatus(String status) {

        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
