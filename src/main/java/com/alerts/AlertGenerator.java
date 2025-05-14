package com.alerts;

import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.factory_method.AlertFactory;
import com.strategy_method.AlertStrategy;
import com.strategy_method.BloodPressureStrategy;
import com.strategy_method.OxygenSaturationStrategy;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private final List<AlertStrategy> strategies = List.of(new BloodPressureStrategy(), new OxygenSaturationStrategy());

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
     * Evaluates the specified patient's data by delegating to each registered
     * AlertStrategy.  Any Alerts produced are passed to {@link #triggerAlert}.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        for (AlertStrategy strategy : strategies) {
            List<Alert> alerts = strategy.checkAlerts(patient, new AlertFactory());
            for (Alert alert : alerts) {
                // wrap in priority, then in repeater
                Alert withPriority = new PriorityAlertDecorator(alert, 1);
                Alert decorated   = new RepeatedAlertDecorator(withPriority,3,30_000L);
                triggerAlert(decorated);
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system.  This method can be extended to
     * notify medical staff, log the alert, or perform other actions.
     *
     * @param alert the alert object containing details about the alert condition
     */
    protected void triggerAlert(Alert alert) {
        // now actually send it (invokes decorators’ send())
        alert.send();
    }
}

