import org.junit.*;

import static edu.gvsu.mipsunit.munit.MUnit.Register.*;
import static edu.gvsu.mipsunit.munit.MUnit.*;

public class Close10Test {

  private void assertClosest(int expected, int first, int second) {
    run("close10", first, second);
    Assert.assertEquals("close10(" + first + ", " + second + ")", expected, get(v0));
  }

  @Test
  public void close10_examples() {
    assertClosest(8, 8, 13);
    assertClosest(8, 13, 8);
    assertClosest(0, 13, 7);
  }

  @Test
  public void close10_exactTen() {
    assertClosest(10, 10, 9);
    assertClosest(10, 11, 10);
    assertClosest(0, 10, 10);
  }

  @Test
  public void close10_ties() {
    assertClosest(0, 8, 12);
    assertClosest(0, 12, 8);
    assertClosest(0, -5, 25);
    assertClosest(0, 7, 7);
    assertClosest(0, -3, -3);
  }

  @Test
  public void close10_bothSidesAndNegativeValues() {
    assertClosest(9, 8, 9);
    assertClosest(9, 9, 8);
    assertClosest(11, 11, 12);
    assertClosest(11, 12, 11);
    assertClosest(-1, -1, -8);
    assertClosest(-1, -8, -1);
    assertClosest(0, 0, -1);
    assertClosest(-1, -1, 30);
  }

  @Test
  public void close10_integerLimits() {
    int[] values = {
        Integer.MIN_VALUE, Integer.MIN_VALUE + 10, -1, 0, 9, 10, 11,
        Integer.MAX_VALUE - 1, Integer.MAX_VALUE
    };
    for (int first : values) {
      for (int second : values) {
        long firstDistance = Math.abs((long) first - 10);
        long secondDistance = Math.abs((long) second - 10);
        int expected = firstDistance == secondDistance ? 0
            : firstDistance < secondDistance ? first : second;
        assertClosest(expected, first, second);
      }
    }
  }
}