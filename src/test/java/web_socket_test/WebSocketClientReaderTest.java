package web_socket_test;

import com.data_management.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketClientReaderTest {

    private DataStorage storage;
    private WebSocketClientReader client;

    @BeforeEach
    public void setup() throws Exception {
        storage = new DataStorage();
        client = new WebSocketClientReader("ws://localhost:8080", storage) {
            @Override
            public boolean isOpen() { return true; }  // override to prevent real connection attempts
        };
    }

    @Test
    public void testValidCSVMessageStored() {
        String message = "1,1716636900000,heart_rate,88.5";
        client.onMessage(message);

        Patient patient = storage.getAllPatients().get(0);
        assertEquals(1, patient.getPatientId());
        assertEquals(1, patient.getRecords(0, System.currentTimeMillis()).size());
        PatientRecord record = patient.getRecords(0, System.currentTimeMillis()).get(0);
        assertEquals("heart_rate", record.getRecordType());
        assertEquals(88.5, record.getMeasurementValue());
    }

    @Test
    public void testSaturationWithPercentage() {
        String message = "2,1716636900000,Saturation,94%";
        client.onMessage(message);

        Patient patient = storage.getAllPatients().get(0);
        PatientRecord record = patient.getRecords(0, System.currentTimeMillis()).get(0);
        assertEquals(94.0, record.getMeasurementValue());
    }

    @Test
    public void testTriggeredAlertMessageStored() {
        String message = "3,1716636900000,Alert,triggered";
        client.onMessage(message);

        Patient patient = storage.getAllPatients().get(0);
        PatientRecord record = patient.getRecords(0, System.currentTimeMillis()).get(0);
        assertEquals("ManualTrigger", record.getRecordType());
        assertEquals(1.0, record.getMeasurementValue());
    }

    @Test
    public void testBadMessageFormatNotStored() {
        String message = "bad,message";
        client.onMessage(message);

        assertEquals(0, storage.getAllPatients().size());
    }

    @Test
    public void testWrongDataTypeNotStored() {
        String message = "4,1716636900000,heart_rate,notANumber";
        client.onMessage(message);

        assertEquals(0, storage.getAllPatients().size());
    }

    @Test
    public void testMissingFieldMessageNotStored() {
        String message = "5,1716636900000,heart_rate";  // missing 4th field
        client.onMessage(message);

        assertEquals(0, storage.getAllPatients().size());
    }
}
