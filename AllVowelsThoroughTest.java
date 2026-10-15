import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;
import edu.mtu.cs.jls.Circuit;
import edu.mtu.cs.jls.JLSInfo;
import edu.mtu.cs.jls.elem.Clock;
import edu.mtu.cs.jls.elem.Element;
import edu.mtu.cs.jls.elem.SubCircuit;
import edu.mtu.cs.jls.sim.Simulator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * HW7a.1 black-box tests run through the unchanged all_vowels_harness.jls.
 * The harness feeds the contents of ROM "Data" to the circuit, one character per clock tick,
 * advances its PC on falling edges, and stops after "NumInputs" characters.
 * Tests inspect clock timing but do not depend on the implementation's internal registers.
 * Timed checks allow propagation delay and restore the original simulator time limit.
 */
public class AllVowelsThoroughTest {

  private static final int[] VOWELS = {97, 101, 105, 111, 117};
  private static final int ALL_SEEN = 31;
  private static final int MAX_INPUTS = 1023;
  private Long originalTimeLimit;
  private long cycleTime;
  private long highTime;
  private long lowTime;

  private Object fieldValue(Class<?> owner, Object instance, String name) throws Exception {
    Field field = owner.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(instance);
  }

  private Clock findClock(Circuit circuit) {
    Clock found = null;
    for (Element element : circuit.getElements()) {
      Clock candidate = null;
      if (element instanceof Clock) {
        candidate = (Clock) element;
      } else if (element instanceof SubCircuit) {
        candidate = findClock(((SubCircuit) element).getSubCircuit());
      }
      if (candidate != null) {
        Assert.assertNull("The harness must use one shared oscillator", found);
        found = candidate;
      }
    }
    return found;
  }

  @Before
  public void configureSimulationTimeLimit() throws Exception {
    originalTimeLimit = (Long) fieldValue(Simulator.class, JLSInfo.sim, "maxTime");
    Circuit circuit = (Circuit) fieldValue(Simulator.class, JLSInfo.sim, "circuit");
    Clock clock = findClock(circuit);
    Assert.assertNotNull("Expected the oscillator supplied inside all_vowels", clock);
    cycleTime = (Integer) fieldValue(Clock.class, clock, "cycleTime");
    highTime = (Integer) fieldValue(Clock.class, clock, "oneTime");
    lowTime = cycleTime - highTime;
    Assert.assertTrue("Clock high and low phases must allow distinct samples",
        highTime > 2 && lowTime > 2);
    JLSInfo.sim.setTimeLimit((MAX_INPUTS + 2L) * cycleTime + 500);
  }

  @After
  public void restoreSimulationTimeLimit() {
    if (originalTimeLimit != null) {
      JLSInfo.sim.setTimeLimit(originalTimeLimit);
    }
  }

  private int[] characters(String input) {
    int[] chars = new int[input.length()];
    for (int index = 0; index < input.length(); index++) {
      chars[index] = input.charAt(index);
    }
    return chars;
  }

  private long runOn(String input) {
    return runOn(characters(input), input.length());
  }

  private void prepareInput(int[] chars, int count) {
    Assert.assertTrue("Input count must fit the harness's 10-bit counter",
        count >= 0 && count <= MAX_INPUTS && count <= chars.length);
    Assert.assertTrue("Data ROM has 1024 entries", chars.length <= 1024);
    for (int character : chars) {
      Assert.assertTrue("Character must fit the 8-bit input: " + character,
          character >= 0 && character <= 255);
    }
    reset();
    if (chars.length > 0) {
      setMemoryUnsigned("Data", 0, chars);
    }
    setRegisterUnsigned("NumInputs", count);
  }

  private long readOutput() {
    boolean output = readPin("all_vowels.Output");
    Assert.assertEquals("Harness output must match the sub-circuit output",
        output, readPin("Output"));
    return output ? 1 : 0;
  }

  private long runOn(int[] chars, int count) {
    prepareInput(chars, count);
    run();
    Assert.assertEquals("Harness stopped before processing the requested characters",
        count, readRegisterUnsigned("PC"));
    Assert.assertTrue("Harness must halt normally, not reach the simulator time limit",
        readRegister("Halt"));
    return readOutput();
  }

  private void expect(long expected, String s) {
    Assert.assertEquals("Input \"" + s + "\"", expected, runOn(s));
  }

  private void expect(long expected, int[] chars, int count) {
    Assert.assertEquals("Input bytes " + Arrays.toString(chars) + ", NumInputs=" + count,
        expected, runOn(chars, count));
  }

  private int vowelBit(int character) {
    switch (character) {
      case 97: return 1;
      case 101: return 2;
      case 105: return 4;
      case 111: return 8;
      case 117: return 16;
      default: return 0;
    }
  }

  private void verifyPrefixes(int[] chars) {
    int seen = 0;
    expect(0, chars, 0);
    for (int count = 1; count <= chars.length; count++) {
      seen |= vowelBit(chars[count - 1]);
      expect(seen == ALL_SEEN ? 1 : 0, chars, count);
    }
  }

  private void verifyTimedStream(int[] chars) {
    long[] offsets = {lowTime - 1, lowTime + highTime / 2, cycleTime - 1};
    int seen = 0;
    for (int processed = 0; processed < chars.length; processed++) {
      for (long offset : offsets) {
        long sampleTime = processed * cycleTime + offset;
        prepareInput(chars, chars.length);
        JLSInfo.sim.setTimeLimit(sampleTime);
        run();
        String context = "Input bytes " + Arrays.toString(chars)
            + ", time=" + sampleTime + ", sampled characters=" + processed;
        Assert.assertEquals(context + ": unexpected harness PC",
            processed, readRegisterUnsigned("PC"));
        Assert.assertFalse(context + ": harness halted prematurely", readRegister("Halt"));
        Assert.assertEquals(context + ": output changed early, late, or lost a seen vowel",
            seen == ALL_SEEN ? 1 : 0, readOutput());
      }
      seen |= vowelBit(chars[processed]);
    }
  }

  private int verifyPermutations(int[] order, int position, int used) {
    if (position == order.length) {
      verifyPrefixes(order);
      return 1;
    }
    int checked = 0;
    for (int index = 0; index < VOWELS.length; index++) {
      if ((used & (1 << index)) == 0) {
        order[position] = VOWELS[index];
        checked += verifyPermutations(order, position + 1, used | (1 << index));
      }
    }
    return checked;
  }

  @Test
  public void emptyString() { expect(0, ""); }

  @Test
  public void noVowels() { expect(0, "rhythm"); }

  @Test
  public void singleVowels() {
    expect(0, "a");
    expect(0, "e");
    expect(0, "i");
    expect(0, "o");
    expect(0, "u");
  }

  @Test
  public void justTheFiveVowels() { expect(1, "aeiou"); }

  @Test
  public void vowelsReversed() { expect(1, "uoiea"); }

  @Test
  public void vowelsScrambled() { expect(1, "oauei"); }

  @Test
  public void missingA() { expect(0, "eiou"); }

  @Test
  public void missingE() { expect(0, "aiou"); }

  @Test
  public void missingI() { expect(0, "aeou"); }

  @Test
  public void missingO() { expect(0, "aeiu"); }

  @Test
  public void missingU() { expect(0, "aeio"); }

  @Test
  public void repeatedVowelsButNotAll() { expect(0, "aaaeeeiiiooo"); }

  @Test
  public void vowelsSeparatedByConsonants() { expect(1, "the quick brown fox jumps over a lazy dog"); }

  @Test
  public void sentenceMissingAVowel() { expect(0, "the quick brown fox jumps over the lazy dog".replace('a', 'x')); }

  @Test
  public void lastCharacterCompletesTheSet() { expect(1, "bcdfg aeio hjk u"); }

  @Test
  public void allButLastCharacterIsNotEnough() { expect(0, "bcdfg aeio hjk"); }

  @Test
  public void firstCharacterCompletesTheSet() { expect(1, "uaeio"); }

  @Test
  public void facetious() { expect(1, "facetious"); }

  @Test
  public void consonantsDontCount() { expect(0, "bcdfghjklmnpqrstvwxyz"); }

  @Test
  public void charactersBeyondNumInputsAreIgnored() {
    verifyPrefixes(VOWELS);
  }

  @Test
  public void longStringWithVowelsOnlyAtTheEnd() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 200; i++) sb.append('z');
    sb.append("aeiou");
    expect(1, sb.toString());
  }

  @Test
  public void longStringWithoutAllVowels() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 100; i++) sb.append("aeio");
    expect(0, sb.toString());
  }

  @Test
  public void all120VowelOrdersAndTheirPrefixes() {
    Assert.assertEquals(120, verifyPermutations(new int[VOWELS.length], 0, 0));
  }

  @Test
  public void all32ObservedVowelCombinations() {
    for (int subset = 0; subset <= ALL_SEEN; subset++) {
      int[] repeated = new int[Integer.bitCount(subset) * 3];
      int position = 0;
      for (int repeat = 0; repeat < 3; repeat++) {
        for (int index = 0; index < VOWELS.length; index++) {
          if ((subset & (1 << index)) != 0) {
            repeated[position++] = VOWELS[index];
          }
        }
      }
      expect(subset == ALL_SEEN ? 1 : 0, repeated, repeated.length);
      int[] completed = Arrays.copyOf(repeated,
          repeated.length + VOWELS.length - Integer.bitCount(subset));
      for (int index = 0; index < VOWELS.length; index++) {
        if ((subset & (1 << index)) == 0) {
          completed[position++] = VOWELS[index];
        }
      }
      verifyPrefixes(completed);
    }
  }

  @Test
  public void everyByteIsComparedExactlyAgainstEachVowel() {
    for (int missing : VOWELS) {
      int[] candidateLast = new int[5];
      int position = 0;
      for (int vowel : VOWELS) {
        if (vowel != missing) {
          candidateLast[position++] = vowel;
        }
      }
      int[] candidateFirst = new int[5];
      System.arraycopy(candidateLast, 0, candidateFirst, 1, 4);
      for (int candidate = 0; candidate <= 255; candidate++) {
        candidateLast[4] = candidate;
        candidateFirst[0] = candidate;
        long expected = candidate == missing ? 1 : 0;
        expect(expected, candidateLast, candidateLast.length);
        expect(expected, candidateFirst, candidateFirst.length);
      }
    }
  }

  @Test
  public void everySingleByteIsInsufficient() {
    for (int candidate = 0; candidate <= 255; candidate++) {
      expect(0, new int[]{candidate}, 1);
    }
  }

  @Test
  public void outputStaysTrueAfterEveryPossibleTrailingByte() {
    int[] chars = Arrays.copyOf(VOWELS, 6);
    for (int candidate = 0; candidate <= 255; candidate++) {
      chars[5] = candidate;
      expect(1, chars, chars.length);
    }
  }

  @Test
  public void uppercaseVowelsDoNotCount() {
    expect(0, "AEIOU");
    expect(0, "aEiOu");
    expect(1, "AEIOUaeiou");
  }

  @Test
  public void mixedByteSequencesMatchIndependentOracleAtEveryPrefix() {
    Random random = new Random(351);
    for (int trial = 0; trial < 32; trial++) {
      int[] chars = new int[32];
      for (int index = 0; index < chars.length; index++) {
        chars[index] = random.nextInt(256);
      }
      if (trial % 2 == 0) {
        for (int index = 0; index < VOWELS.length; index++) {
          chars[index * 5 + 1] = VOWELS[index];
        }
      }
      verifyPrefixes(chars);
    }
  }

  @Test
  public void aNewSimulationDoesNotInheritPreviouslySeenVowels() {
    for (int missing : VOWELS) {
      expect(1, "aeiou");
      expect(0, "");
      int[] incomplete = new int[4];
      int position = 0;
      for (int vowel : VOWELS) {
        if (vowel != missing) {
          incomplete[position++] = vowel;
        }
      }
      expect(0, incomplete, incomplete.length);
    }
  }

  @Test
  public void outputChangesOnlyAfterTheCompletingCharacterIsClocked() {
    for (int lastVowel : VOWELS) {
      int[] chars = new int[10];
      int position = 0;
      for (int vowel : VOWELS) {
        if (vowel != lastVowel) {
          chars[position++] = vowel;
        }
      }
      chars[4] = 'z';
      chars[5] = lastVowel;
      chars[6] = 0;
      chars[7] = lastVowel;
      chars[8] = 255;
      chars[9] = 'z';
      verifyTimedStream(chars);
    }
  }

  @Test
  public void outputRemainsLowBetweenEdgesWhenAVowelIsMissing() {
    verifyTimedStream(characters("aeioAEIOUaeiozz"));
  }

  @Test
  public void maximumLengthCompletesOnTheFinalCharacter() {
    int[] chars = new int[MAX_INPUTS];
    Arrays.fill(chars, 'z');
    System.arraycopy(VOWELS, 0, chars, MAX_INPUTS - VOWELS.length, VOWELS.length);
    expect(0, chars, MAX_INPUTS - 1);
    expect(1, chars, MAX_INPUTS);
  }

  @Test
  public void maximumLengthMissingEachVowelStaysFalse() {
    for (int missing : VOWELS) {
      int[] chars = new int[MAX_INPUTS];
      for (int index = 0; index < chars.length; index++) {
        int candidate = VOWELS[index % VOWELS.length];
        chars[index] = candidate == missing ? 'z' : candidate;
      }
      expect(0, chars, chars.length);
    }
  }

  @Test
  public void maximumLengthRetainsAllVowelsThroughLongByteSuffix() {
    int[] chars = new int[MAX_INPUTS];
    System.arraycopy(VOWELS, 0, chars, 0, VOWELS.length);
    for (int index = VOWELS.length; index < chars.length; index++) {
      chars[index] = (index - VOWELS.length) % 256;
    }
    expect(1, chars, chars.length);
  }
}