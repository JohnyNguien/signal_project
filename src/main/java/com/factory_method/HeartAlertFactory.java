// src/main/java/com/factory_method/HeartRateAlertType.java
package com.factory_method;

import com.alerts.Alert;

public class HeartAlertFactory implements AlertType {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, condition, timestamp);
    }
}
