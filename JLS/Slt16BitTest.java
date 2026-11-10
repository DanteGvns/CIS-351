import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Tests signed and unsigned comparisons for Slt_16bit.jls.
 */
public class Slt16BitTest {

  public static final long[] testIntegers = {
      0, 1, 2, 13, 127, 128, 129, 255, 256, 257, 0x5555,
      32766, 32767, 32768, 32769, 0xAAAA, 65279, 65280, 65281,
      65407, 65408, 65409, 65534, 65535
  };

  private void verify(long a, long b, boolean signed) {
    long interpretedA = signed ? (short) a : a;
    long interpretedB = signed ? (short) b : b;

    if (signed) {
      setPinSigned("InputA", interpretedA);
      setPinSigned("InputB", interpretedB);
    } else {
      setPinUnsigned("InputA", interpretedA);
      setPinUnsigned("InputB", interpretedB);
    }
    setPin("Signed", signed);
    run();

    String message = String.format("%d < %d (%s): ", interpretedA, interpretedB,
        signed ? "signed" : "unsigned");
    Assert.assertEquals("LessThan " + message, interpretedA < interpretedB,
        readPin("LessThan"));
  }

  private void verifyBothModes(long a, long b) {
    verify(a, b, false);
    verify(a, b, true);
  }

  @Test
  public void zeroAndEquality() {
    for (long value : testIntegers) {
      verifyBothModes(value, value);
      verifyBothModes(value, 0);
      verifyBothModes(0, value);
    }
  }

  @Test
  public void signedSubtractionOverflow() {
    verify(32767, 65535, true);
    verify(32768, 1, true);
    verify(0, 32768, true);
    verify(32767, 32768, true);
    verify(32768, 32767, true);
    verify(65535, 32767, true);
  }

  @Test
  public void signednessChangesComparison() {
    verifyBothModes(65535, 0);
    verifyBothModes(0, 65535);
    verifyBothModes(32768, 32767);
    verifyBothModes(32767, 32768);
    verifyBothModes(1, 65535);
    verifyBothModes(65535, 1);
  }

  @Test
  public void unsignedDifferenceSignIsNotLessThan() {
    verify(0, 65535, false);
    verify(65535, 0, false);
    verify(1, 65535, false);
    verify(65535, 1, false);
  }

  @Test
  public void testAll() {
    int count = 0;
    for (long a : testIntegers) {
      for (long b : testIntegers) {
        verifyBothModes(a, b);
        count += 2;
      }
    }
    System.out.println("testAll ran " + count + " circuit checks.");
  }

  @Test
  public void compareAcrossEveryBit() {
    for (int bit = 0; bit < 16; bit++) {
      long singleBit = 1L << bit;
      long lowerBits = singleBit - 1;
      verifyBothModes(lowerBits, singleBit);
      verifyBothModes(singleBit, lowerBits);
      verifyBothModes(singleBit, 0);
      verifyBothModes(0, singleBit);
      verifyBothModes(singleBit, 0xFFFF ^ singleBit);
      verifyBothModes(0xFFFF ^ singleBit, singleBit);
    }
  }

  @Test
  public void repeatableRandomInputs() {
    Random random = new Random(351L);
    for (int trial = 0; trial < 512; trial++) {
      long a = random.nextInt(65536);
      long b = random.nextInt(65536);
      verifyBothModes(a, b);
      verifyBothModes(b, a);
      verifyBothModes(a, a);
    }
  }
}