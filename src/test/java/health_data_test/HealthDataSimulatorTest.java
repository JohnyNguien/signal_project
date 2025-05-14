package health_data_test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cardio_generator.HealthDataSimulator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;

public class HealthDataSimulatorTest {
    @BeforeEach
    void resetStatics() throws Exception {
        Field pc = HealthDataSimulator.class.getDeclaredField("patientCount");
        pc.setAccessible(true);
        pc.setInt(null, 50);
        Field os = HealthDataSimulator.class.getDeclaredField("outputStrategy");
        os.setAccessible(true);
        os.set(null, new ConsoleOutputStrategy());
    }

    @Test
    void testSingletonInstance() {
        HealthDataSimulator a = HealthDataSimulator.getInstance();
        HealthDataSimulator b = HealthDataSimulator.getInstance();
        assertSame(a, b, "getInstance() should always return the same instance");
    }

    @Test
    void testInitializePatientIds() {
        List<Integer> ids = HealthDataSimulator.initializePatientIds(5);
        assertEquals(5, ids.size(), "List size should match requested count");
        for (int i = 1; i <= 5; i++) {
            assertEquals(i, ids.get(i - 1), "IDs should be sequential from 1 to N");
        }
    }

    @Test
    void testParseArgumentsSetsPatientCount() throws Exception {
        Method m = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        m.setAccessible(true);
        m.invoke(null, (Object) new String[] { "--patient-count", "10" });

        Field pc = HealthDataSimulator.class.getDeclaredField("patientCount");
        pc.setAccessible(true);
        assertEquals(10, pc.getInt(null), "patientCount should be updated to 10");
    }

    @Test
    void testParseArgumentsSetsOutputStrategyConsole() throws Exception {
        Method m = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        m.setAccessible(true);
        m.invoke(null, (Object) new String[] { "--output", "console" });

        Field os = HealthDataSimulator.class.getDeclaredField("outputStrategy");
        os.setAccessible(true);
        Object strat = os.get(null);
        assertNotNull(strat);
        assertTrue(strat instanceof ConsoleOutputStrategy, "outputStrategy should be ConsoleOutputStrategy");
    }

    @Test
    void testParseArgumentsInvalidPatientCountKeepsDefault() throws Exception {
        Method m = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        m.setAccessible(true);
        m.invoke(null, (Object) new String[] { "--patient-count", "notANumber" });

        Field pc = HealthDataSimulator.class.getDeclaredField("patientCount");
        pc.setAccessible(true);
        assertEquals(50, pc.getInt(null), "Invalid count should leave patientCount at default 50");
    }
}
