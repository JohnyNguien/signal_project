package data_management;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class DataStorageTest {
    private DataStorage storage;

    @BeforeEach
    void setUp() throws Exception {
        storage = DataStorage.getInstance();
        // clear the singleton’s internal map via reflection so each test is isolated
        Field mapField = DataStorage.class.getDeclaredField("patientMap");
        mapField.setAccessible(true);
        Map<?, ?> map = (Map<?, ?>) mapField.get(storage);
        map.clear();
    }

    @Test
    void testSingletonInstance() {
        DataStorage other = DataStorage.getInstance();
        assertSame(storage, other, "getInstance() should always return the same instance");
    }

    @Test
    void testAddAndRetrieveRecords() {
        int patientId = 1;
        long t1 = 1_000L, t2 = 2_000L;
        storage.addPatientData(patientId, 98.6, "HeartRate", t1);
        storage.addPatientData(patientId, 120.0, "BloodPressure", t2);

        List<PatientRecord> records = storage.getRecords(patientId, 0L, 3_000L);
        assertEquals(2, records.size(), "Should retrieve both records");

        boolean foundHR = records.stream()
            .anyMatch(r -> "HeartRate".equals(r.getRecordType())
                        && r.getMeasurementValue() == 98.6
                        && r.getTimestamp() == t1);
        boolean foundBP = records.stream()
            .anyMatch(r -> "BloodPressure".equals(r.getRecordType())
                        && r.getMeasurementValue() == 120.0
                        && r.getTimestamp() == t2);

        assertTrue(foundHR && foundBP, "Both HeartRate and BloodPressure records must be present");
    }

    @Test
    void testGetRecordsOutsideRange() {
        int patientId = 2;
        storage.addPatientData(patientId, 75.0, "HeartRate", 5_000L);
        List<PatientRecord> records = storage.getRecords(patientId, 6_000L, 7_000L);
        assertTrue(records.isEmpty(), "No records should be returned for non-overlapping time range");
    }

    @Test
    void testGetAllPatients() {
        storage.addPatientData(3, 80.0, "HeartRate", 1_234L);
        storage.addPatientData(4, 85.0, "HeartRate", 5_678L);
        List<Patient> patients = storage.getAllPatients();
        assertEquals(2, patients.size(), "There should be two distinct patients stored");
        assertTrue(patients.stream().anyMatch(p -> p.getPatientId() == 3));
        assertTrue(patients.stream().anyMatch(p -> p.getPatientId() == 4));
    }
}
