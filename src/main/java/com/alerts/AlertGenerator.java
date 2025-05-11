package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.factory_method.AlertFactory;
import com.factory_method.AlertType;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Implementation goes here
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        List<Double> systolic = new ArrayList<>();
        List<Double> diastolic = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();
        List<Double> bloodSaturation = new ArrayList<>();
        List<Double> ECG = new ArrayList<>();
        boolean lowBP = false;
        boolean lowO2 = false;

        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double value = record.getMeasurementValue();
            long ts = record.getTimestamp();

            //Critical Treshold Alert(Systolic 90<BP<180)
            if (type.equals("StolicBloodPressure")){
                lowBP = false;
                systolic.add(value);
                timestamps.add(ts);
                if (value > 180) {
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()), type + " Too High", ts));
                }
                else if (value < 90) {
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()), type + " Too Low", ts));
                    lowBP = true;
                }
            }

            //Critical Treshold Alert(Diastolic 60<BP<120)
            else if (type.equals("DiastolicBloodPressure")){
                diastolic.add(value);
                if (value > 120) {
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()), type + " Too High", ts));
                }
                else if (value < 60) {
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()), type + " Too Low", ts));
                }
            }

            //Critical Treshold Blood Saturation bellow 92%
            else if (type.equals("BloodSaturation")) {
                lowO2 = false;
                bloodSaturation.add(value);
                if (value < 92) {
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()), type + " Too Low", ts));
                    lowO2 = true;
                }
            }
            else if (lowBP && lowO2) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Hypotensive Hypoxemia", ts));
            }
            else if (type.equals("ECG")){
                ECG.add(value);
            }

        }
        //Trend Alert Systolic
        bloodPressureTrend(patient.getPatientId(), systolic, timestamps, "SystolicBloodPressure");

        //Trend Alert Diastolic
        bloodPressureTrend(patient.getPatientId(), diastolic, timestamps, "DiastolicBloodPressure");

        //Trend Alert Blood Oxygen
        bloodSaturationTrend(patient.getPatientId(), bloodSaturation, timestamps, "BloodSaturation");

    }

    public void bloodPressureTrend(int patientId, List<Double> values, List<Long> ts, String type){
        if (values.size()<3) {
            return;
        }
        for(int i = 0; i <= values.size() - 4; i++) {
            double v1 = values.get(i);
            double v2 = values.get(i + 1);
            double v3 = values.get(i + 2);
            double v4 = values.get(i + 3);

            if ((v2 - v1> 10 && v3 - v2> 10 && v4 - v3> 10)) {
                triggerAlert(new Alert(String.valueOf(patientId), type + " Too High", ts.get(i + 3)));
            }
            else if ((v1 - v2 > 10 && v2 - v3 > 10 && v3 - v4> 10)) {
                triggerAlert(new Alert(String.valueOf(patientId), type + " Too Low", ts.get(i + 3)));
            }
        }
    }


    public void bloodSaturationTrend(int patientId, List<Double> values, List<Long> timestamps, String type) {
        if (values.size() < 2) return;

        for (int i = 1; i < values.size(); i++) {
            double previous = values.get(i - 1);
            double current = values.get(i);
            long timeDiff = timestamps.get(i) - timestamps.get(i - 1);

            if (timeDiff <= 600_000 && previous - current >= 5.0) {
                triggerAlert(new Alert(String.valueOf(patientId), type + " Dropped Too Fast", timestamps.get(i)));
            }
        }
    }

    public void ECGTrend(int patientId, List<Double> values, List<Long> timestamps, String type){
        //assuming to do the window on 5 values
        if (values.size() < 5) return;


    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        String condition_type = alert.getCondition();
        AlertFactory alertFactory = new AlertFactory();

        AlertType alertType = alertFactory.getType(condition_type);
        alertType.createAlert(alert.getPatientId(), alert.getCondition(), alert.getTimestamp());

    }
}

