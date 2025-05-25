package com.data_management;

import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.client.WebSocketClient;


import java.net.URI;

public class WebSocketClient implements  DataReader {
    private WebSocketClient client;

    @Override
    public void start(DataStorage dataStorage) {

        try {
        client = new WebSocketClient(new URI("ws://localhost:8080")) {

            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to WebSocket server.");
            }

            public void onMessage(String message) {
                try {
                    String[] parts = message.split(",");

                    int patientId = Integer.parseInt(parts[0].trim());
                    long timestamp = Long.parseLong(parts[1].trim());
                    String recordType = parts[2].trim();
                    String rawValue = parts[3].trim();

                    double value;
                    if (recordType.equals("Saturation") && rawValue.endsWith("%")) {
                        value = Double.parseDouble(rawValue.replace("%", ""));
                    } else if (recordType.equalsIgnoreCase("Alert") && rawValue.equalsIgnoreCase("triggered")) {
                        dataStorage.addPatientData(patientId, 1.0, "ManualTrigger", timestamp);
                        return;
                    } else {
                        value = Double.parseDouble(rawValue);
                    }

                    dataStorage.addPatientData(patientId, value, recordType, timestamp);

                } catch (Exception e) {
                    System.err.println("Failed to parse message: " + message);
                    e.printStackTrace();
                }
            }

            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Connection closed: " + reason);
            }

            public void onError(Exception ex) {
                System.err.println("WebSocket error: " + ex.getMessage());
            }
        };

        client.connect();

    } catch (Exception e) {
        System.err.println("Failed to connect to WebSocket server.");
        e.printStackTrace();
    }
}
}
