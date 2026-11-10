import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Sample test cases for "split" mode.
 *
 * IMPORTANT:  These test cases do *not* thoroughly test split mode.  They are
 * here to show you how to set the pins and how to compute the expected output.
 * Thinking up the test cases themselves is your job.  You need to re-name this
 * class, add to `segmentValues`, and write more tests.
 *
 * Remember that a circuit can be completely correct when `Split` is 0 and
 * completely wrong when `Split` is 1.  Passing the un-split tests tells you
 * nothing about split mode.
 *
 * Throughout, a packed 16-bit value is written [high|low]:  the value
 * [5|7] is 0x0507, whose high segment is 5 and whose low segment is 7.
 */
public class SampleSplitSigned16BitAdderTest {

  protected static final int SEG_BITS = 8;
  protected static final long SEG_MASK = (1L << SEG_BITS) - 1;   // 0xFF

  // The complete list of 8-bit segment values to be tested.
  // (IMPORTANT !!! You need to add to this list !!!)
  //
  // Which 8-bit values are worth trying?  Think about what has to happen inside
  // your adder at the boundary between bit 7 and bit 8, and about which
  // segment `CarryIn` belongs to.
  protected static final long[] segmentValues = {0, 1, 2, 3};

  // Combine an 8-bit high segment and an 8-bit low segment into one 16-bit value.
  protected static long pack(long high, long low) {
    return ((high & SEG_MASK) << SEG_BITS) | (low & SEG_MASK);
  }

  // Pull the segments back out of a 16-bit value.  (Handy when writing your own
  // assertions or debugging output.)
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
   * @param carryIn the carry in to the *low* segment
   */
  protected static void verifySplit(long aHigh, long aLow, long bHigh, long bLow, boolean carryIn) {

    //////////////////////////////////
    //
    // Compute the expected outputs
    //
    /////////////////////////////////

    // In split mode the two segments are added completely independently.
    // `CarryIn` belongs to the low segment; the high segment always adds with a
    // carry in of 0.
    long lowSum = aLow + bLow + (carryIn ? 1 : 0);
    long highSum = aHigh + bHigh;

    // `pack` discards everything above bit 7 of each segment, which is exactly
    // what the circuit must do:  a carry out of the low segment is thrown away
    // rather than added into bit 8.
    long expected = pack(highSum, lowSum);

    // `CarryOut` is the carry out of the *high* segment.
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
    Assert.assertEquals("CarryOut " + message, expectedCarryOut, readPin("CarryOut"));

    // `Overflow` is not checked in split mode.
  }

  //
  // A couple of examples, to show how `verifySplit` is called.
  // These are nowhere near enough.
  //

  @Test
  public void zero_zero_false() {
    verifySplit(0, 0, 0, 0, false);
  }

  @Test
  public void zero_zero_true() {
    // The last argument is the carry in to the low segment.
    verifySplit(0, 0, 0, 0, true);
  }

  @Test
  public void independentSegments_false() {
    // [5|7] + [3|2] == [8|9]
    verifySplit(5, 7, 3, 2, false);
  }


  // This is actually rather gross; but, it is an effective way to thoroughly
  // test split mode without having to write hundreds of individual methods.
  // Note that it tries every combination of *four* segments, so the list it
  // walks grows quickly.  Keep `segmentValues` well chosen rather than huge.
  @Test
  public void testAll() {
    for (long aHigh : segmentValues) {
      for (long aLow : segmentValues) {
        for (long bHigh : segmentValues) {
          for (long bLow : segmentValues) {
            verifySplit(aHigh, aLow, bHigh, bLow, false);
            verifySplit(aHigh, aLow, bHigh, bLow, true);
          }
        }
      }
    }
  } // end testAll
}
