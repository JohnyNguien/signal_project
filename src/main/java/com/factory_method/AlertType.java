package com.factory_method;

public interface AlertType {
    void createAlert(String patientId, String condition, long timestamp);
}
