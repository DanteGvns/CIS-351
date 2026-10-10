import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Tests the HW6a.4 characteristic table using InputB, Clock, outputA0, and outputA1.
 */
public class CharacteristicTableTest {

  private static final long SETTLE_TIME = 100;
  private static final boolean[] NEXT_A0 = {
      false, true, true, false, true, true, false, false
  };
  private static final boolean[] NEXT_A1 = {
      true, false, true, false, false, true, false, true
  };

  private void settle() {
    run(SETTLE_TIME);
  }

  private int state() {
    return (readPin("outputA0") ? 2 : 0) | (readPin("outputA1") ? 1 : 0);
  }

  private void verifyPulse(boolean inputB) {
    setPin("Clock", false);
    settle();
    int before = state();

    setPin("InputB", inputB);
    settle();
    Assert.assertEquals("State changed with Clock held low", before, state());

    int row = (inputB ? 4 : 0) | before;
    String message = String.format("B=%d, A0=%d, A1=%d: ", inputB ? 1 : 0,
        before >> 1, before & 1);

    setPin("Clock", true);
    settle();
    Assert.assertEquals(message + "next A0", NEXT_A0[row], readPin("outputA0"));
    Assert.assertEquals(message + "next A1", NEXT_A1[row], readPin("outputA1"));
    int after = state();

    setPin("InputB", !inputB);
    settle();
    Assert.assertEquals(message + "state changed with Clock held high", after, state());

    setPin("Clock", false);
    settle();
    Assert.assertEquals(message + "state changed on falling edge", after, state());
  }

  private void reachState(int target) {
    setPin("Clock", false);
    settle();
    for (int attempt = 0; attempt < 4 && state() != target; attempt++) {
      verifyPulse(false);
    }
    Assert.assertEquals("Could not reach starting state " + target + " using B=0",
        target, state());
  }

  @Test
  public void allEightTableRows() {
    for (int before = 0; before < 4; before++) {
      reachState(before);
      verifyPulse(false);
      reachState(before);
      verifyPulse(true);
    }
    System.out.println("Checked all 8 characteristic-table rows and clock-hold behavior.");
  }

  @Test
  public void zeroInputCyclesThroughAllStates() {
    reachState(0);
    int[] expectedStates = {1, 2, 3, 0};
    for (int expected : expectedStates) {
      verifyPulse(false);
      Assert.assertEquals("B=0 four-state cycle", expected, state());
    }
  }

  @Test
  public void oneInputTogglesOnlyA0() {
    for (int before = 0; before < 4; before++) {
      reachState(before);
      verifyPulse(true);
      Assert.assertEquals("B=1 must toggle A0 and hold A1", before ^ 2, state());
      verifyPulse(true);
      Assert.assertEquals("Two B=1 pulses must restore the starting state", before, state());
    }
  }
}