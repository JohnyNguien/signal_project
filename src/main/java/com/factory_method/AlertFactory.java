package com.factory_method;

public class AlertFactory {

    /**
     * Factory Method lookup: returns the right subclass instance
     * based on your record‐type string.
     */
    public static AlertType getFactory(String recordType) {
        switch (recordType) {
            case "SystolicPressure":
                return new BloodPressureAlertFactory();
            case "DiastolicPressure":
                return new BloodPressureAlertFactory();
            case "Saturation":
                return new BloodOxygenAlertFactory();
            case "ECG":
                return new ECGAlertFactory();
            case "HeartRate":
                return new ECGAlertFactory();
            default:
                throw new IllegalArgumentException("Unknown record type: " + recordType);
        }
    }

    public AlertType getType(String recordType) {
        return AlertFactory.getFactory(recordType);
    }

}
