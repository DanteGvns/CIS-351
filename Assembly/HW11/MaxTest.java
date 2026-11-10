import org.junit.*;

import static edu.gvsu.mipsunit.munit.MUnit.Register.*;
import static edu.gvsu.mipsunit.munit.MUnit.*;

public class MaxTest {

  private void assertMax(int expected, int... values) throws Exception {
    Label array = wordData(values);
    run("max", array, values.length);
    Assert.assertEquals(expected, get(v0));
    Assert.assertArrayEquals(values, getWords(array, 0, values.length));
  }

  @Test
  public void max_emptyDoesNotReadMemory() {
    run("max", 0, 0);
    Assert.assertEquals(0, get(v0));
  }

  @Test
  public void max_singleElement() throws Exception {
    assertMax(7, 7);
    assertMax(-7, -7);
    assertMax(Integer.MIN_VALUE, Integer.MIN_VALUE);
  }

  @Test
  public void max_eachPosition() throws Exception {
    assertMax(9, 9, 4, 2);
    assertMax(9, 4, 9, 2);
    assertMax(9, 4, 2, 9);
  }

  @Test
  public void max_ties() throws Exception {
    assertMax(7, 7, 7, 7);
    assertMax(9, 9, 2, 9);
  }

  @Test
  public void max_allNegative() throws Exception {
    assertMax(-1, -1, -5, -3);
    assertMax(-1, -5, -1, -3);
    assertMax(-1, -5, -3, -1);
  }

  @Test
  public void max_zeroAndMixedSigns() throws Exception {
    assertMax(0, -5, 0, -3);
    assertMax(6, -8, 0, 6, -2);
    assertMax(0, 0, 0, 0);
  }

  @Test
  public void max_respectsLength() throws Exception {
    Label array = wordData(-5, -2, 1000);
    run("max", array, 2);
    Assert.assertEquals(-2, get(v0));
    Assert.assertArrayEquals(new int[] {-5, -2, 1000}, getWords(array, 0, 3));
  }

  @Test
  public void max_integerLimits() throws Exception {
    assertMax(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, -1);
    assertMax(Integer.MIN_VALUE + 1, Integer.MIN_VALUE, Integer.MIN_VALUE + 1);
    assertMax(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
}