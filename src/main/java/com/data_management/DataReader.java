package com.data_management;

import java.io.IOException;

public interface DataReader {
    void start(DataStorage storage) throws IOException;
}
