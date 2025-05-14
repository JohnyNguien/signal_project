// BloodPressureStrategy.java
package com.strategy_method;

import java.util.ArrayList;
import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.factory_method.AlertFactory;
import com.factory_method.AlertType;

public class BloodPressureStrategy implements AlertStrategy {
    private static final String SYSTOLIC = "SystolicPressure";
    private static final String DIASTOLIC = "DiastolicPressure";

    @Override
    public List<Alert> checkAlerts(Patient patient, AlertFactory factory) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        
        List<Double> sysVals = new ArrayList<>();
        List<Long> sysTs = new ArrayList<>();
        List<Double> diaVals = new ArrayList<>();
        List<Long> diaTs = new ArrayList<>();
        String patientId = String.valueOf(patient.getPatientId());

        for (PatientRecord rec : records) {
            String type = rec.getRecordType();
            double value = rec.getMeasurementValue();
            long timestamp = rec.getTimestamp();

            if (type.equals(SYSTOLIC)) {
                sysVals.add(value);
                sysTs.add(timestamp);
                if (value > 180) {
                    AlertType alertType = factory.getType(SYSTOLIC);
                    Alert alert = alertType.createAlert(patientId, SYSTOLIC + " Too High", timestamp);
                    alerts.add(alert);
                } else if (value < 90) {
                    AlertType alertType = factory.getType(SYSTOLIC);
                    Alert alert = alertType.createAlert(patientId, SYSTOLIC + " Too Low", timestamp);
                    alerts.add(alert);
                }
            } else if (type.equals(DIASTOLIC)) {
                diaVals.add(value);
                diaTs.add(timestamp);
                if (value > 120) {
                    AlertType alertType = factory.getType(DIASTOLIC);
                    Alert alert = alertType.createAlert(patientId, DIASTOLIC + " Too High", timestamp);
                    alerts.add(alert);
                } else if (value < 60) {
                    AlertType alertType = factory.getType(DIASTOLIC);
                    Alert alert = alertType.createAlert(patientId, DIASTOLIC + " Too Low", timestamp);
                    alerts.add(alert);
                }
            }
        }

        for (int i = 0; i <= sysVals.size()-4; i++) {
            double v1 = sysVals.get(i);
            double v2 = sysVals.get(i+1);
            double v3 = sysVals.get(i+2);
            double v4 = sysVals.get(i+3);
            long time = sysTs.get(i+3);

            if ((v2-v1 > 10) && (v3-v2 > 10) && (v4-v3 > 10)) {
                AlertType alertType = factory.getType(SYSTOLIC);
                Alert alert = alertType.createAlert(patientId, SYSTOLIC + " Trend Too High", time);
                alerts.add(alert);
            } else if ((v1-v2 > 10) && (v2-v3 > 10) && (v3-v4 > 10)) {
                AlertType alertType = factory.getType(SYSTOLIC);
                Alert alert = alertType.createAlert(patientId, SYSTOLIC + " Trend Too Low", time);
                alerts.add(alert);
            }
        }

        for (int i = 0; i <= diaVals.size()-4; i++) {
            double v1 = diaVals.get(i);
            double v2 = diaVals.get(i+1);
            double v3 = diaVals.get(i+2);
            double v4 = diaVals.get(i+3);
            long time = diaTs.get(i+3);

            if ((v2-v1 > 10) && (v3-v2 > 10) && (v4-v3 > 10)) {
                AlertType alertType = factory.getType(DIASTOLIC);
                Alert alert = alertType.createAlert(patientId, DIASTOLIC + " Trend Too High", time);
                alerts.add(alert);
            } else if ((v1-v2 > 10) && (v2-v3 > 10) && (v3-v4 > 10)) {
                AlertType alertType = factory.getType(DIASTOLIC);
                Alert alert = alertType.createAlert(patientId, DIASTOLIC + " Trend Too Low", time);
                alerts.add(alert);
            }
        }

        return alerts;
    }
}