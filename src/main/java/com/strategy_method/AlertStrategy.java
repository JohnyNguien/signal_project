package com.strategy_method;

import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.factory_method.AlertFactory;

public interface AlertStrategy {
    List<Alert> checkAlerts(Patient patient, AlertFactory factory);
}