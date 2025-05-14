package com.factory_method;

import com.alerts.Alert;

public class BloodPressureAlertFactory implements AlertType {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, condition, timestamp);
    }
}