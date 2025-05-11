package com.data_management;

import java.io.*;

public class FileDataReader implements DataReader{
    private String path;
    public FileDataReader(String path){
        this.path = path;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File folder = new File(path);
        File[] files = folder.listFiles();

        for (File file : files){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine())!=null){
                String[] commaPart = line.split(",");
                int patientID = Integer.parseInt(commaPart[0]);
                String recordType = commaPart[1];
                double value = Double.parseDouble(commaPart[2]);
                long timeStamp = Long.parseLong(commaPart[3]);
                dataStorage.addPatientData(patientID, value, recordType, timeStamp);
            }
            reader.close();
        }
    }
}
