import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Test cases for a signed, split-capable 16-bit adder.
 */
public class SignedSplit16BitAdderTest {

  // Values chosen to test signed boundaries, carries, and alternating bit patterns.
  public static final long testIntegers[] = {
      -32768, -32767, -256, -129, -128, -127, -2, -1,
      0, 1, 2, 13, 127, 128, 129, 0x5555, 0x7FFE, 32767
  };

  protected static final int SEG_BITS = 8;
  protected static final long SEG_MASK = (1L << SEG_BITS) - 1; // 0xFF

  // Kept intentionally small because testAllSplit tries every combination of four
  // segments. These values cover zero, carry boundaries, sign-bit boundaries,
  // alternating bits, and the maximum 8-bit value.
  protected static final long[] segmentValues = {
      0, 1, 2, 7, 127, 128, 129, 0xAA, 254, 255
  };

  // Helper method that runs an ordinary signed 16-bit test.
  protected static void verify(long a, long b, boolean carryIn) {

    //////////////////////////////////
    //
    // Compute the expected outputs
    //
    /////////////////////////////////
    long carryInAsInt = (carryIn ? 1 : 0);
    long expected = a + b + carryInAsInt;
    boolean expectedOverflow = ((expected >= (1 << 15)) || (expected < -(1 << 15)));

    long unsignedA = a & 0xFFFF;
    long unsignedB = b & 0xFFFF;
    boolean expectedCarryOut = (unsignedA + unsignedB + carryInAsInt) > 0xFFFF;

    // Output wraps around if there is a signed overflow.
    if (expectedOverflow && expected > 0) {
      expected -= 65536;
    } else if (expectedOverflow && expected < 0) {
      expected += 65536;
    }

    ////////////////////////////////////////
    //
    // Configure and simulate the circuit
    //
    ///////////////////////////////////////
    setPinSigned("InputA", a);
    setPinSigned("InputB", b);
    setPin("CarryIn", carryIn);
    setPin("Split", false);
    run();

    ////////////////////////////////////////
    //
    // Check the correctness of the output
    //
    ///////////////////////////////////////
    String message = "of " + a + " + " + b + " with " + (carryIn ? "a " : " no ") + " carry in";
    Assert.assertEquals("Output " + message, expected, readPinSigned("Output"));
    Assert.assertEquals("CarryOut " + message, expectedCarryOut, readPin("CarryOut"));
    Assert.assertEquals("Overflow " + message, expectedOverflow, readPin("Overflow"));
  }

  // Combine an 8-bit high segment and an 8-bit low segment into one 16-bit value.
  protected static long pack(long high, long low) {
    return ((high & SEG_MASK) << SEG_BITS) | (low & SEG_MASK);
  }

  protected static long highOf(long packed) {
    return (packed >> SEG_BITS) & SEG_MASK;
  }

  protected static long lowOf(long packed) {
    return packed & SEG_MASK;
  }

  /**
   * Helper method that runs one split-mode test.
   *
   * @param aHigh   the high 8 bits of InputA
   * @param aLow    the low 8 bits of InputA
   * @param bHigh   the high 8 bits of InputB
   * @param bLow    the low 8 bits of InputB
   * @param carryIn the carry in to the low segment
   */
  protected static void verifySplit(long aHigh, long aLow, long bHigh, long bLow, boolean carryIn) {

    //////////////////////////////////
    //
    // Compute the expected outputs
    //
    /////////////////////////////////
    long lowSum = aLow + bLow + (carryIn ? 1 : 0);
    long highSum = aHigh + bHigh;
    long expected = pack(highSum, lowSum);
    boolean expectedCarryOut = (highSum > SEG_MASK);

    ////////////////////////////////////////
    //
    // Configure and simulate the circuit
    //
    ///////////////////////////////////////
    setPinUnsigned("InputA", pack(aHigh, aLow));
    setPinUnsigned("InputB", pack(bHigh, bLow));
    setPin("CarryIn", carryIn);
    setPin("Split", true);
    run();

    ////////////////////////////////////////
    //
    // Check the correctness of the output
    //
    ///////////////////////////////////////
    String message = String.format("of [%d|%d] + [%d|%d] with %s carry in (Split == 1)",
        aHigh, aLow, bHigh, bLow, (carryIn ? "a" : "no"));

    Assert.assertEquals("Output " + message, expected, readPinUnsigned("Output"));
    Assert.assertEquals("High segment " + message, highOf(expected), highOf(readPinUnsigned("Output")));
    Assert.assertEquals("Low segment " + message, lowOf(expected), lowOf(readPinUnsigned("Output")));
    Assert.assertEquals("CarryOut " + message, expectedCarryOut, readPin("CarryOut"));

    // Overflow is intentionally not checked in split mode.
  }

  //
  // Quick ordinary-mode tests
  //

  @Test
  public void zero_zero_false() {
    verify(0, 0, false);
  }

  @Test
  public void zero_one_true() {
    verify(0, 1, true);
  }

  @Test
  public void positiveOverflow() {
    verify(32767, 1, false);
  }

  @Test
  public void negativeOverflow() {
    verify(-32768, -1, false);
  }

  @Test
  public void differentSignsNoOverflow() {
    verify(-1, 1, false);
  }

  @Test
  public void testAllSigned() {
    int count = 0;
    for (long a : testIntegers) {
      for (long b : testIntegers) {
        verify(a, b, false);
        verify(a, b, true);
        count += 2;
      }
    }
    System.out.println("testAllSigned ran " + count + " circuit checks.");
  }

  //
  // Quick split-mode tests
  //

  @Test
  public void split_zero_zero_false() {
    verifySplit(0, 0, 0, 0, false);
  }

  @Test
  public void split_zero_zero_true() {
    verifySplit(0, 0, 0, 0, true);
  }

  @Test
  public void split_discardsLowCarry() {
    // [5|255] + [3|1] == [8|0], not [9|0].
    verifySplit(5, 255, 3, 1, false);
  }

  @Test
  public void split_carryInOnlyAffectsLowSegment() {
    // CarryIn belongs only to the low segment.
    verifySplit(5, 255, 3, 0, true);
  }

  @Test
  public void split_highCarryOut() {
    // CarryOut comes from the high segment, not the low segment.
    verifySplit(255, 0, 1, 0, false);
  }

  @Test
  public void split_lowCarryDoesNotSetCarryOut() {
    verifySplit(0, 255, 0, 1, false);
  }

  @Test
  public void testAllSplit() {
    int count = 0;
    for (long aHigh : segmentValues) {
      for (long aLow : segmentValues) {
        for (long bHigh : segmentValues) {
          for (long bLow : segmentValues) {
            verifySplit(aHigh, aLow, bHigh, bLow, false);
            verifySplit(aHigh, aLow, bHigh, bLow, true);
            count += 2;
          }
        }
      }
    }
    System.out.println("testAllSplit ran " + count + " circuit checks.");
  }
}
