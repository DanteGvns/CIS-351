import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * DLUnit tests for all_vowels.jls, run through all_vowels_harness.jls.
 * The harness feeds the contents of ROM "Data" to the circuit, one character per clock tick,
 * and stops after "NumInputs" characters.
 */
public class AllVowelsTest {

  // Load the string into the ROM, tell the harness how many characters to feed, run,
  // and return the circuit's final output.
  private long runOn(String s) {
    int[] chars = new int[s.length()];
    for (int i = 0; i < s.length(); i++) {
      chars[i] = s.charAt(i);
    }
    if (chars.length > 0) {
      setMemoryUnsigned("Data", 0, chars);
    }
    setRegisterUnsigned("NumInputs", s.length());
    run();
    return readPinUnsigned("all_vowels.Output");
  }

  private void expect(long expected, String s) {
    Assert.assertEquals("Input \"" + s + "\"", expected, runOn(s));
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
    setMemoryUnsigned("Data", 0, new int[]{'a', 'e', 'i', 'o', 'u'});
    setRegisterUnsigned("NumInputs", 4);
    run();
    Assert.assertEquals(0, readPinUnsigned("all_vowels.Output"));
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
}
