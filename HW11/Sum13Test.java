import org.junit.*;

import static edu.gvsu.mipsunit.munit.MUnit.Register.*;
import static edu.gvsu.mipsunit.munit.MUnit.*;

public class Sum13Test {

  private void assertSum(int expected, int... values) throws Exception {
    Label array = wordData(values);
    run("sum13", array, values.length);
    Assert.assertEquals(expected, get(v0));
    Assert.assertArrayEquals(values, getWords(array, 0, values.length));
  }

  @Test
  public void sum13_emptyDoesNotReadMemory() {
    run("sum13", 0, 0);
    Assert.assertEquals(0, get(v0));
  }

  @Test
  public void sum13_sumsAllIfNo13() throws Exception {
    assertSum(6, 1, 2, 2, 1);
    assertSum(2, 1, 1);
  }

  @Test
  public void sum13_singleElement() throws Exception {
    assertSum(7, 7);
    assertSum(0, 13);
  }

  @Test
  public void sum13_single13AtEnd() throws Exception {
    assertSum(6, 1, 2, 2, 1, 13);
  }

  @Test
  public void sum13_ignore13_andFollowing() throws Exception {
    assertSum(4, 1, 13, 2, 3);
    assertSum(3, 13, 100, 1, 2);
  }

  @Test
  public void sum13_13InPenultimatePosition() throws Exception {
    assertSum(3, 1, 2, 13, 100);
  }

  @Test
  public void sum13_two13s() throws Exception {
    assertSum(6, 1, 13, 100, 2, 13, 200, 3);
    assertSum(0, 13, 1, 13);
  }

  @Test
  public void sum13_consecutive13s() throws Exception {
    assertSum(0, 13, 13);
    assertSum(0, 13, 13, 13);
  }

  @Test
  public void sum13_negativeAndZero() throws Exception {
    assertSum(-8, -5, 0, -3);
    assertSum(-7, -13, 13, -100, 6);
    assertSum(0, 0, 0, 0);
  }

  @Test
  public void sum13_respectsSize() throws Exception {
    Label array = wordData(1, 2, 1000);
    run("sum13", array, 2);
    Assert.assertEquals(3, get(v0));
  }

  @Test
  public void sum13_zeroSizeIgnoresContents() throws Exception {
    Label array = wordData(1000);
    run("sum13", array, 0);
    Assert.assertEquals(0, get(v0));
  }

  @Test
  public void sum13_javaIntegerOverflow() throws Exception {
    assertSum(Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
    assertSum(Integer.MAX_VALUE, Integer.MIN_VALUE, -1);
  }
}