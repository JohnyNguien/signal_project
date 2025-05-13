package com.factory_method;

public class AlertFactory {
    public static AlertType getType(String condition){
        if (condition.contains("SystolicBloodPressure") || condition.contains("DiastolicBloodPressure")){
            return new BloodPressureAlertFactory();
        } else if (condition.contains("BloodSaturation")){
            return new BloodOxygenAlertFactory();
        } else if (condition.contains("ECG")){
            return new ECGAlertFactory();
        }
        return null;
    }
}
