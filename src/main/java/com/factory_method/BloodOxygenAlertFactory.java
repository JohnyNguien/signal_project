package com.factory_method;

public class BloodOxygenAlertFactory implements AlertType {

    @Override
    public void createAlert(String patientId, String condition, long timestamp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
