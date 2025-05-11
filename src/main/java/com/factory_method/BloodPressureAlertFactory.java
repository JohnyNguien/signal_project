package com.factory_method;

public class BloodPressureAlertFactory implements AlertType {

    @Override
    public void createAlert(String patientId, String condition, long timestamp) {
        System.out.println(patientId + condition + "at " + timestamp);
    }
    
}
