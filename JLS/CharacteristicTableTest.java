import java.lang.reflect.Field;
import edu.mtu.cs.jls.Circuit;
import edu.mtu.cs.jls.JLSInfo;
import edu.mtu.cs.jls.elem.Clock;
import edu.mtu.cs.jls.elem.Element;
import edu.mtu.cs.jls.elem.Register;
import edu.mtu.cs.jls.sim.Simulator;
import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Tests HW6a.4 using InputB, outputA0, outputA1, and registers clock and clock2.
 * Requires rising-edge D flip-flops and an oscillator with cycle=1000, one=500.
 * Checks four pulses per row without changing the circuit's clock configuration.
 */
public class CharacteristicTableTest {

  private static final int[] NEXT_STATE = {1, 2, 3, 0, 2, 3, 0, 1};

  private Object fieldValue(Class<?> owner, Object instance, String name) throws Exception {
    Field field = owner.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(instance);
  }

  private void checkSetup() throws Exception {
    Field circuitField = Simulator.class.getDeclaredField("circuit");
    circuitField.setAccessible(true);
    Circuit circuit = (Circuit) circuitField.get(JLSInfo.sim);
    int clockCount = 0;
    int registerCount = 0;
    for (Element element : circuit.getElements()) {
      if (element instanceof Clock) {
        Clock oscillator = (Clock) element;
        int cycle = (Integer) fieldValue(Clock.class, oscillator, "cycleTime");
        int highTime = (Integer) fieldValue(Clock.class, oscillator, "oneTime");
        Assert.assertEquals("Clock setup: set oscillator cycle to 1000 time units", 1000, cycle);
        Assert.assertEquals("Clock setup: set oscillator high time to 500 time units", 500, highTime);
        clockCount++;
      }
      if (element instanceof Register) {
        Register register = (Register) element;
        String name = register.getName();
        Assert.assertTrue("Unexpected register '" + name + "'; expected clock or clock2",
            name.equals("clock") || name.equals("clock2"));
        Assert.assertEquals("Register " + name + " must be 1 bit", 1, register.getBits());
        Enum<?> type = (Enum<?>) fieldValue(Register.class, register, "type");
        Assert.assertEquals("Register " + name
            + " must be a rising-edge D flip-flop, not a latch or falling-edge flip-flop",
            "PosFF", type.name());
        Assert.assertTrue("Register " + name + " delay must be between 1 and 50 time units",
            register.getDelay() > 0 && register.getDelay() <= 50);
        registerCount++;
      }
    }
    Assert.assertEquals("Setup error: expected one built-in oscillator, not a Clock input pin. "
        + "Table behavior has not been checked yet.",
        1, clockCount);
    Assert.assertEquals("Expected two registers named clock (A0) and clock2 (A1)",
        2, registerCount);
  }

  private void verify(boolean inputB, boolean a0, boolean a1,
      boolean nextA0, boolean nextA1) throws Exception {
    checkSetup();
    long[] sampleTimes = {400, 600, 800, 1200, 1600, 1800, 2200,
        2600, 2800, 3200, 3600, 3800, 4200};
    StringBuilder failures = new StringBuilder();
    long originalLimit = (Long) fieldValue(Simulator.class, JLSInfo.sim, "maxTime");
    try {
      for (long sampleTime : sampleTimes) {
        reset();
        setRegister("clock", a0);
        setRegister("clock2", a1);
        setPin("InputB", inputB);
        JLSInfo.sim.setTimeLimit(sampleTime);
        run();

        int pulses = sampleTime < 500 ? 0 : (int) ((sampleTime - 500) / 1000) + 1;
        int expected = (a0 ? 2 : 0) | (a1 ? 1 : 0);
        if (pulses > 0) {
          expected = (nextA0 ? 2 : 0) | (nextA1 ? 1 : 0);
          for (int pulse = 1; pulse < pulses; pulse++) {
            expected = NEXT_STATE[(inputB ? 4 : 0) | expected];
          }
        }
        boolean actualA0 = readPin("outputA0");
        boolean actualA1 = readPin("outputA1");
        int actual = (actualA0 ? 2 : 0) | (actualA1 ? 1 : 0);
        if (expected != actual) {
          failures.append(String.format(
              "B=%d, initial(A0,A1)=(%d,%d), time=%d, rising edges=%d: "
                  + "expected=(%d,%d), actual=(%d,%d), registers=(%d,%d)%n",
              inputB ? 1 : 0, a0 ? 1 : 0, a1 ? 1 : 0, sampleTime, pulses,
              expected >> 1, expected & 1, actualA0 ? 1 : 0, actualA1 ? 1 : 0,
              readRegister("clock") ? 1 : 0, readRegister("clock2") ? 1 : 0));
        }
      }
    } finally {
      JLSInfo.sim.setTimeLimit(originalLimit);
    }
    Assert.assertTrue("State mismatch. Check mux -> clock.D, bottom XOR -> clock2.D, "
        + "and oscillator -> both C pins. These are diagnostic suggestions, not a wiring diagnosis.\n"
        + failures, failures.length() == 0);
    System.out.printf("PASS B=%d, initial(A0,A1)=(%d,%d): %d samples across 4 pulses.%n",
        inputB ? 1 : 0, a0 ? 1 : 0, a1 ? 1 : 0, sampleTimes.length);
  }

  @Test
  public void b0_a0_0_a1_0() throws Exception {
    verify(false, false, false, false, true);
  }

  @Test
  public void b0_a0_0_a1_1() throws Exception {
    verify(false, false, true, true, false);
  }

  @Test
  public void b0_a0_1_a1_0() throws Exception {
    verify(false, true, false, true, true);
  }

  @Test
  public void b0_a0_1_a1_1() throws Exception {
    verify(false, true, true, false, false);
  }

  @Test
  public void b1_a0_0_a1_0() throws Exception {
    verify(true, false, false, true, false);
  }

  @Test
  public void b1_a0_0_a1_1() throws Exception {
    verify(true, false, true, true, true);
  }

  @Test
  public void b1_a0_1_a1_0() throws Exception {
    verify(true, true, false, false, false);
  }

  @Test
  public void b1_a0_1_a1_1() throws Exception {
    verify(true, true, true, false, true);
  }
}