import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Dante Givens - CIS 351 Project 3.
 * Black-box tests for ALU_16Bit.jls.
 */
public class ALU16BitTest {

  public static class OpCodes {
    public static final int ADDU = 0;
    public static final int SUBU = 1;
    public static final int AND = 2;
    public static final int OR = 3;
    public static final int NOT = 4;
    public static final int XOR = 5;
    public static final int LUI = 6;
    public static final int SLTU = 7;
    public static final int ADD = 8;
    public static final int SUB = 9;
    public static final int SLT = 15;
  }

  private static final int[] operations = {
      OpCodes.ADDU, OpCodes.SUBU, OpCodes.AND, OpCodes.OR, OpCodes.NOT,
      OpCodes.XOR, OpCodes.LUI, OpCodes.SLTU, OpCodes.ADD, OpCodes.SUB, OpCodes.SLT
  };

  public static final long[] testIntegers = {
      0, 1, 2, 3, 7, 8, 15, 16, 31, 32,
      127, 128, 129, 254, 255, 256, 257, 4095, 4096,
      0x5555, 0xAAAA, 32766, 32767, 32768, 32769,
      0xFEFF, 0xFF00, 0xFF01, 65533, 65534, 65535
  };

  private void verify(long a, long b, int op) {
    long unsignedA = a & 0xFFFF;
    long unsignedB = b & 0xFFFF;
    long signedA = (short) unsignedA;
    long signedB = (short) unsignedB;
    long expected;
    boolean expectedOverflow = false;

    switch (op) {
      case OpCodes.ADDU:
        expected = unsignedA + unsignedB;
        break;
      case OpCodes.SUBU:
        expected = unsignedA - unsignedB;
        break;
      case OpCodes.AND:
        expected = unsignedA & unsignedB;
        break;
      case OpCodes.OR:
        expected = unsignedA | unsignedB;
        break;
      case OpCodes.NOT:
        expected = ~unsignedA;
        break;
      case OpCodes.XOR:
        expected = unsignedA ^ unsignedB;
        break;
      case OpCodes.LUI:
        expected = (unsignedA & 0xFF) << 8;
        break;
      case OpCodes.SLTU:
        expected = unsignedA < unsignedB ? 1 : 0;
        break;
      case OpCodes.ADD:
        expected = signedA + signedB;
        expectedOverflow = expected < -32768 || expected > 32767;
        break;
      case OpCodes.SUB:
        expected = signedA - signedB;
        expectedOverflow = expected < -32768 || expected > 32767;
        break;
      case OpCodes.SLT:
        expected = signedA < signedB ? 1 : 0;
        break;
      default:
        throw new IllegalArgumentException("Undefined ALU opcode: " + op);
    }

    setPinUnsigned("InputA", unsignedA);
    setPinUnsigned("InputB", unsignedB);
    setPinUnsigned("Op", op);
    run();

    String message = String.format("Op=%d A=0x%04X (%d signed) B=0x%04X (%d signed): ",
        op, unsignedA, signedA, unsignedB, signedB);
    Assert.assertEquals("Output " + message, expected & 0xFFFF, readPinUnsigned("Output"));
    Assert.assertEquals("Overflow " + message, expectedOverflow, readPin("Overflow"));
  }

  private void verifyAllPairs(int op) {
    int count = 0;
    for (long a : testIntegers) {
      for (long b : testIntegers) {
        verify(a, b, op);
        count++;
      }
    }
    System.out.println("Op " + op + " boundary pairs ran " + count + " circuit checks.");
  }

  @Test
  public void testAddu() { verifyAllPairs(OpCodes.ADDU); }

  @Test
  public void testSubu() { verifyAllPairs(OpCodes.SUBU); }

  @Test
  public void testAnd() { verifyAllPairs(OpCodes.AND); }

  @Test
  public void testOr() { verifyAllPairs(OpCodes.OR); }

  @Test
  public void testNot() { verifyAllPairs(OpCodes.NOT); }

  @Test
  public void testXor() { verifyAllPairs(OpCodes.XOR); }

  @Test
  public void testLui() { verifyAllPairs(OpCodes.LUI); }

  @Test
  public void testSltu() { verifyAllPairs(OpCodes.SLTU); }

  @Test
  public void testSignedAddition() { verifyAllPairs(OpCodes.ADD); }

  @Test
  public void testSignedSubtraction() { verifyAllPairs(OpCodes.SUB); }

  @Test
  public void testSlt() { verifyAllPairs(OpCodes.SLT); }

  @Test
  public void everyInputBitAndCarryBoundary() {
    int count = 0;
    for (int bit = 0; bit < 16; bit++) {
      long singleBit = 1L << bit;
      long lowerBits = singleBit - 1;
      long complement = 0xFFFF ^ singleBit;
      for (int op : operations) {
        verify(singleBit, 0, op);
        verify(0, singleBit, op);
        verify(singleBit, singleBit, op);
        verify(singleBit, complement, op);
        verify(complement, singleBit, op);
        verify(lowerBits, 1, op);
        verify(1, lowerBits, op);
        verify(singleBit, 1, op);
        verify(1, singleBit, op);
        count += 9;
      }
    }
    System.out.println("everyInputBitAndCarryBoundary ran " + count + " circuit checks.");
  }

  @Test
  public void everyPairOfSingleBitPositions() {
    for (int firstBit = 0; firstBit < 16; firstBit++) {
      for (int secondBit = 0; secondBit < 16; secondBit++) {
        for (int op : operations) {
          verify(1L << firstBit, 1L << secondBit, op);
        }
      }
    }
    System.out.println("everyPairOfSingleBitPositions ran 2816 circuit checks.");
  }

  @Test
  public void everyLuiByteAndIgnoredInputs() {
    int count = 0;
    for (int lowByte = 0; lowByte < 256; lowByte++) {
      for (int highByte : new int[] { 0, 1, 0x55, 0xAA, 0xFF }) {
        long a = (highByte << 8) | lowByte;
        for (long b : new long[] { 0, 0xFFFF, 0x55AA }) {
          verify(a, b, OpCodes.LUI);
          verify(a, b, OpCodes.NOT);
          count += 2;
        }
      }
    }
    System.out.println("everyLuiByteAndIgnoredInputs ran " + count + " circuit checks.");
  }

  @Test
  public void comparisonsAndArithmeticAroundEveryBoundary() {
    for (long boundary : testIntegers) {
      for (int offset = -1; offset <= 1; offset++) {
        long neighbor = (boundary + offset) & 0xFFFF;
        for (int op : new int[] {
            OpCodes.ADDU, OpCodes.SUBU, OpCodes.ADD, OpCodes.SUB, OpCodes.SLTU, OpCodes.SLT }) {
          verify(boundary, neighbor, op);
          verify(neighbor, boundary, op);
        }
      }
    }
    System.out.println("comparisonsAndArithmeticAroundEveryBoundary ran 1116 circuit checks.");
  }

  @Test
  public void everyOpcodeTransitionAndOverflowClearing() {
    int count = 0;
    for (int previousOp : operations) {
      for (int nextOp : operations) {
        for (long[] pair : new long[][] {
            { 0x7FFF, 1 }, { 0x8000, 0xFFFF }, { 0x7FFF, 0xFFFF }, { 0x8000, 1 } }) {
          verify(pair[0], pair[1], previousOp);
          verify(pair[0], pair[1], nextOp);
          count += 2;
        }
      }
    }
    System.out.println("everyOpcodeTransitionAndOverflowClearing ran " + count + " circuit checks.");
  }

  @Test
  public void repeatableRandomInputs() {
    Random random = new Random(351L);
    int count = 0;
    for (int trial = 0; trial < 1024; trial++) {
      long a = random.nextInt(65536);
      long b = random.nextInt(65536);
      for (int op : operations) {
        verify(a, b, op);
        count++;
      }
      verify(a, a, OpCodes.SLT);
      verify(a, a, OpCodes.SLTU);
      count += 2;
    }
    System.out.println("repeatableRandomInputs ran " + count + " circuit checks.");
  }
}