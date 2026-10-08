import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Tests a 16-bit conditional inverter with InputA, Op, and OutputA pins.
 */
public class ConditionalInvert16BitTest {

  private void verify(long input, boolean op) {
    long expected = op ? input ^ 0xFFFF : input;

    setPinUnsigned("InputA", input);
    setPin("Op", op);
    run();

    String message = String.format("InputA=0x%04X, Op=%d: ", input, op ? 1 : 0);
    Assert.assertEquals("OutputA " + message, expected, readPinUnsigned("OutputA"));
  }

  @Test
  public void zeroAndAllOnes() {
    verify(0, false);
    verify(0, true);
    verify(0xFFFF, false);
    verify(0xFFFF, true);
  }

  @Test
  public void alternatingBitsAndSignBoundary() {
    long[] inputs = {0x5555, 0xAAAA, 0x7FFF, 0x8000};
    for (long input : inputs) {
      verify(input, false);
      verify(input, true);
    }
  }

  @Test
  public void everyBitPosition() {
    for (int bit = 0; bit < 16; bit++) {
      long singleBit = 1L << bit;
      long allButOneBit = 0xFFFF ^ singleBit;
      verify(singleBit, false);
      verify(singleBit, true);
      verify(allButOneBit, false);
      verify(allButOneBit, true);
    }
  }

  @Test
  public void changingOpWithoutChangingInput() {
    long[] inputs = {0, 1, 0x1234, 0x5555, 0xAAAA, 0x8000, 0xFFFF};
    for (long input : inputs) {
      verify(input, false);
      verify(input, true);
      verify(input, false);
    }
  }

  @Test
  public void testAll() {
    int count = 0;
    for (long input = 0; input <= 0xFFFF; input++) {
      verify(input, false);
      verify(input, true);
      count += 2;
    }
    System.out.println("testAll ran " + count + " circuit checks.");
  }
}