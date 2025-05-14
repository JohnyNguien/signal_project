package com.strategy_method;

import java.util.ArrayList;
import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.factory_method.AlertFactory;
import com.factory_method.AlertType;

public class OxygenSaturationStrategy implements AlertStrategy {
    private static final String TYPE = "Saturation";
    private static final double CRITICAL_LOW = 92.0;

    @Override
    public List<Alert> checkAlerts(Patient patient, AlertFactory factory) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        
        List<Double> values = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        String patientId = String.valueOf(patient.getPatientId());

        for (PatientRecord record : records) {
            if (record.getRecordType().equals(TYPE)) {
                double currentValue = record.getMeasurementValue();
                long currentTime = record.getTimestamp();
                
                values.add(currentValue);
                times.add(currentTime);
                
                if (currentValue < CRITICAL_LOW) {
                    AlertType alertType = factory.getType(TYPE);
                    Alert alert = alertType.createAlert(patientId, TYPE + " Too Low", currentTime);
                    alerts.add(alert);
                }
            }
        }

        for (int i = 1; i < values.size(); i++) {
            long previousTime = times.get(i-1);
            long currentTime = times.get(i);
            long timeDifference = currentTime - previousTime;
            
            double previousValue = values.get(i-1);
            double currentValue = values.get(i);
            double valueDrop = previousValue - currentValue;
            
            if (timeDifference <= 600000) {  // 10 minutes in milliseconds
                if (valueDrop >= 5.0) {
                    AlertType alertType = factory.getType(TYPE);
                    Alert alert = alertType.createAlert(patientId, TYPE + " Dropped Too Fast", currentTime);
                    alerts.add(alert);
                }
            }
        }

        return alerts;
    }
}