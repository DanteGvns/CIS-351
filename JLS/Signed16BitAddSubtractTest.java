import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Tests Output, Overflow, and UnsignedOverflow for Subtractor_16bit.jls.
 */
public class Signed16BitAddSubtractTest {

  public static final long[] testIntegers = {
      -32768, -32767, -21846, -257, -256, -255, -129, -128, -127, -2, -1,
      0, 1, 2, 13, 127, 128, 129, 255, 256, 257, 0x5555, 32766, 32767
  };

  private void verify(long a, long b, boolean op) {
    long result = op ? a - b : a + b;
    long expected = (short) result;
    boolean expectedOverflow = result < -32768 || result > 32767;
    long unsignedA = a & 0xFFFF;
    long unsignedB = b & 0xFFFF;
    boolean expectedUnsignedOverflow = op
        ? unsignedA < unsignedB
        : unsignedA + unsignedB > 0xFFFF;

    setPinSigned("InputA", a);
    setPinSigned("InputB", b);
    setPin("Op", op);
    run();

    String message = a + (op ? " - " : " + ") + b + ": ";
    Assert.assertEquals("Output " + message, expected, readPinSigned("Output"));
    Assert.assertEquals("Overflow " + message, expectedOverflow, readPin("Overflow"));
    Assert.assertEquals("UnsignedOverflow " + message, expectedUnsignedOverflow,
        readPin("UnsignedOverflow"));
  }

  @Test
  public void zeroAndEquality() {
    verify(0, 0, false);
    verify(0, 0, true);
    verify(0, 1, true);
    verify(1, 0, true);
    verify(-1, -1, true);
    verify(-32768, -32768, true);
    verify(32767, 32767, true);
  }

  @Test
  public void signedOverflowBoundaries() {
    verify(32767, 1, false);
    verify(-32768, -1, false);
    verify(32767, -1, true);
    verify(-32768, 1, true);
    verify(0, -32768, true);
    verify(32767, -32768, true);
    verify(-32768, 32767, true);
    verify(-1, -32768, true);
    verify(-32768, -32768, false);
    verify(-32768, 32767, false);
  }

  @Test
  public void unsignedOverflowIsNotResultSign() {
    verify(32767, 1, false);
    verify(-1, 1, false);
    verify(-32768, 0, true);
    verify(0, -1, true);
    verify(-1, 0, true);
    verify(-1, -1, false);
  }

  @Test
  public void testAll() {
    int count = 0;
    for (long a : testIntegers) {
      for (long b : testIntegers) {
        verify(a, b, false);
        verify(a, b, true);
        count += 2;
      }
    }
    System.out.println("testAll ran " + count + " circuit checks.");
  }

  @Test
  public void carryAndBorrowAcrossEveryBit() {
    for (int bit = 0; bit < 16; bit++) {
      long singleBit = 1L << bit;
      long lowerBits = singleBit - 1;
      verify((short) lowerBits, 1, false);
      verify((short) singleBit, 1, true);
      verify(0, (short) singleBit, true);
      verify((short) singleBit, (short) singleBit, true);
      verify((short) singleBit, (short) (0xFFFF ^ singleBit), false);
    }
  }

  @Test
  public void repeatableRandomInputs() {
    Random random = new Random(351L);
    for (int trial = 0; trial < 512; trial++) {
      long a = (short) random.nextInt(65536);
      long b = (short) random.nextInt(65536);
      verify(a, b, false);
      verify(a, b, true);
    }
  }
}