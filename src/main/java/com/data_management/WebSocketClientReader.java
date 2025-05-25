package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClientReader extends WebSocketClient implements DataReader {

    private final DataStorage dataStorage;

    public WebSocketClientReader(String serverUri, DataStorage dataStorage) throws URISyntaxException {
        super(new URI(serverUri));
        this.dataStorage = dataStorage;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket server.");
    }

    @Override
    public void onMessage(String message) {
        try {
            String[] parts = message.split(",");

            if (parts.length != 4) {
                System.err.println("Wrong message format: " + message);
                return;
            }

            int patientId = Integer.parseInt(parts[0].trim());
            long timestamp = Long.parseLong(parts[1].trim());
            String recordType = parts[2].trim();
            String rawValue = parts[3].trim();

            double value;
            if (recordType.equals("Saturation") && rawValue.endsWith("%")) {
                value = Double.parseDouble(rawValue.replace("%", ""));
            }
            else if (recordType.equalsIgnoreCase("Alert") && rawValue.equalsIgnoreCase("triggered")) {
                dataStorage.addPatientData(patientId, 1.0, "ManualTrigger", timestamp);
                return;
            }
            else {
                value = Double.parseDouble(rawValue);
            }

            dataStorage.addPatientData(patientId, value, recordType, timestamp);

        }
        catch (Exception e) {
            System.err.println("Failed to read message: " + message);
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
        reconnectWithDelay();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
        reconnectWithDelay();
    }

    private void reconnectWithDelay() {
        new Thread(() -> {
            try {
                System.out.println("Reconnecting in 5 seconds...");
                Thread.sleep(5000);
                this.reconnect();
            } catch (InterruptedException ignored) {}
        }).start();
    }

    @Override
    public void start(DataStorage storage) {
        this.connect();
    }
}
