package com;

import com.cardio_generator.outputs.WebSocketOutputStrategy;
import com.data_management.DataStorage;
import com.data_management.WebSocketClientReader;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        //run the server
        WebSocketOutputStrategy server = new WebSocketOutputStrategy(8080);
        DataStorage storage = DataStorage.getInstance();
        //connect to it
        WebSocketClientReader client = new WebSocketClientReader("ws://localhost:8080", storage);
        client.start(storage);
    }
}