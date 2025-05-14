package com.alerts;

// Represents an alert
public class Alert{
    private String patientId;
    private String condition;
    private long timestamp;

    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void send() {
        // your “default” alert behavior:
        System.out.printf(
          "ALERT → patient=%s, condition=%s, at=%d%n",
          patientId, condition, timestamp
        );
    }
}
