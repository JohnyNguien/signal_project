package com.data_management;

import java.io.*;

//outdated class just to show how it looked
public class FileDataReader{
    private String path;
    public FileDataReader(String path){
        this.path = path;
    }


    public void readData(DataStorage dataStorage) throws IOException {
        File folder = new File(path);
        File[] files = folder.listFiles();

        if (files == null)
            return;

        for (File file : files){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine())!=null){
                String[] commaPart = line.split(",");

                int patientID = Integer.parseInt(commaPart[0].trim());
                String recordType = commaPart[1].trim();
                String stringValue =  commaPart[2].trim();
                long timeStamp = Long.parseLong(commaPart[3].trim());

                double value;
                if (recordType.equals("Saturation") && stringValue.endsWith("%")) {
                    value = Double.parseDouble(stringValue.replace("%", ""));
                }
                else if (recordType.equals("Alert") && stringValue.equalsIgnoreCase("triggered")) {
                    dataStorage.addPatientData(patientID, 1.0, "ManualTrigger", timeStamp);
                    continue;
                }
                else {
                    value = Double.parseDouble(stringValue);
                }
                dataStorage.addPatientData(patientID, value, recordType, timeStamp);
            }
            reader.close();
        }
    }
}
