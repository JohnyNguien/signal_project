package alerts;

import com.data_management.*;
import org.junit.jupiter.api.Test;
import  com.alerts.*;
import static org.junit.jupiter.api.Assertions.*;

public class AlertGeneratorTest {
    static class TestableAlertGenerator extends AlertGenerator {
        public Alert lastAlert = null;
        public TestableAlertGenerator(DataStorage ds) { super(ds); }
        @Override
        protected void triggerAlert(Alert alert) { this.lastAlert = alert; }
    }

    @Test
    void testLowSystolicBloodPressureAllerts(){
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 85.0, "SystolicPressure", now);
        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNotNull(generator.lastAlert);
        assertEquals("SystolicPressure Too Low", generator.lastAlert.getCondition());
        assertEquals("1", generator.lastAlert.getPatientId());
        assertEquals(now, generator.lastAlert.getTimestamp());
    }
    @Test
    void testHighSystolicBloodPressureAllerts(){
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 185.0, "SystolicPressure", now);
        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNotNull(generator.lastAlert);
        assertEquals("SystolicPressure Too High", generator.lastAlert.getCondition());
        assertEquals("1", generator.lastAlert.getPatientId());
        assertEquals(now, generator.lastAlert.getTimestamp());
    }
    @Test
    void testNormalSystolicBloodPressureAllerts(){
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 105.0, "SystolicPressure", now);
        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNull(generator.lastAlert);
    }
    @Test
    void testLowDiastolicBloodPressureAllerts(){
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 55, "DiastolicPressure", now);
        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNotNull(generator.lastAlert);
        assertEquals("DiastolicPressure Too Low", generator.lastAlert.getCondition());
        assertEquals("1", generator.lastAlert.getPatientId());
        assertEquals(now, generator.lastAlert.getTimestamp());
    }
    @Test
    void testHighDiastolicBloodPressureAllerts(){
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 125, "DiastolicPressure", now);
        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNotNull(generator.lastAlert);
        assertEquals("DiastolicPressure Too High", generator.lastAlert.getCondition());
        assertEquals("1", generator.lastAlert.getPatientId());
        assertEquals(now, generator.lastAlert.getTimestamp());
    }
    @Test
    void testNormalDiastolicBloodPressureAllerts(){
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 100, "DiastolicPressure", now);
        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNull(generator.lastAlert);

    }
    @Test
    void testLowSaturationAllerts(){
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 91, "Saturation", now);
        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNotNull(generator.lastAlert);
        assertEquals("Saturation Too Low", generator.lastAlert.getCondition());
        assertEquals("1", generator.lastAlert.getPatientId());
        assertEquals(now, generator.lastAlert.getTimestamp());
    }
    @Test
    void testNormalSaturationAllerts(){
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 95, "Saturation", now);
        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNull(generator.lastAlert);
    }
    @Test
    void testIncreasingSystolicPressureAlert() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 100.0, "SystolicPressure", now);
        storage.addPatientData(1, 115.0, "SystolicPressure", now + 1);
        storage.addPatientData(1, 130.0, "SystolicPressure", now + 2);
        storage.addPatientData(1, 145.0, "SystolicPressure", now + 3);

        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNotNull(generator.lastAlert);
        assertTrue(generator.lastAlert.getCondition().contains("Too High"));
    }
    @Test
    void testDecreasingSystolicPressureTrendAlert() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 150.0, "SystolicPressure", now);
        storage.addPatientData(1, 135.0, "SystolicPressure", now + 1);
        storage.addPatientData(1, 120.0, "SystolicPressure", now + 2);
        storage.addPatientData(1, 105.0, "SystolicPressure", now + 3);

        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNotNull(generator.lastAlert);
        assertTrue(generator.lastAlert.getCondition().contains("Too Low"));
    }

    @Test
    void testNoTrendDoesNotTriggerAlert() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 120.0, "SystolicPressure", now);
        storage.addPatientData(1, 125.0, "SystolicPressure", now + 1);
        storage.addPatientData(1, 127.0, "SystolicPressure", now + 2);
        storage.addPatientData(1, 130.0, "SystolicPressure", now + 3);

        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNull(generator.lastAlert);
    }
    @Test
    void testRapidDropInSaturationAlert() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 97.0, "Saturation", now);
        storage.addPatientData(1, 91.5, "Saturation", now + 300_000);

        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNotNull(generator.lastAlert);
        assertEquals("Saturation Dropped Too Fast", generator.lastAlert.getCondition());
    }
    @Test
    void testSaturationDropLessThan5PercentDoesNotTriggerAlert() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 97.0, "Saturation", now);
        storage.addPatientData(1, 93.0, "Saturation", now + 300_000);

        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNull(generator.lastAlert);
    }
    @Test
    void testSaturationDropAfterTenMinutesDoesNotTriggerAlert() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, 97.0, "Saturation", now);
        storage.addPatientData(1, 90.0, "Saturation", now + 700_000);

        TestableAlertGenerator generator = new TestableAlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertNull(generator.lastAlert);
    }


}
