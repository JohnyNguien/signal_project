package com.factory_method;

import com.alerts.Alert;

public interface AlertType {
    Alert createAlert(String patientId, String condition, long timestamp);
}
