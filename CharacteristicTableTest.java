import java.lang.reflect.Field;
import edu.mtu.cs.jls.Circuit;
import edu.mtu.cs.jls.JLSInfo;
import edu.mtu.cs.jls.elem.Clock;
import edu.mtu.cs.jls.elem.Element;
import edu.mtu.cs.jls.sim.Simulator;
import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Tests HW6a.4 using InputB, outputA0, outputA1, and registers clock and clock2.
 * Uses the existing oscillator with test-only timing; circuit files are unchanged.
 */
public class CharacteristicTableTest {

  private void configureClock() throws Exception {
    Field circuitField = Simulator.class.getDeclaredField("circuit");
    circuitField.setAccessible(true);
    Circuit circuit = (Circuit) circuitField.get(JLSInfo.sim);
    int clockCount = 0;
    for (Element element : circuit.getElements()) {
      if (element instanceof Clock) {
        Clock oscillator = (Clock) element;
        oscillator.setValue("cycle", 1000);
        oscillator.setValue("one", 500);
        clockCount++;
      }
    }
    Assert.assertEquals("Expected one built-in oscillator connected to both registers",
        1, clockCount);
  }

  private void verify(boolean inputB, boolean a0, boolean a1,
      boolean nextA0, boolean nextA1) throws Exception {
    configureClock();
    long[] sampleTimes = {400, 600, 700, 800, 1200};
    for (long sampleTime : sampleTimes) {
      reset();
      setRegister("clock", a0);
      setRegister("clock2", a1);
      setPin("InputB", inputB);
      JLSInfo.sim.setTimeLimit(sampleTime);
      run();

      boolean beforeFirstEdge = sampleTime < 500;
      String message = String.format("B=%d, A0=%d, A1=%d, time=%d: ",
          inputB ? 1 : 0, a0 ? 1 : 0, a1 ? 1 : 0, sampleTime);
      Assert.assertEquals(message + "outputA0", beforeFirstEdge ? a0 : nextA0,
          readPin("outputA0"));
      Assert.assertEquals(message + "outputA1", beforeFirstEdge ? a1 : nextA1,
          readPin("outputA1"));
    }
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