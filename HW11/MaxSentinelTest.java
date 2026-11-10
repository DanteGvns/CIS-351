import org.junit.*;

import static edu.gvsu.mipsunit.munit.MUnit.Register.*;
import static edu.gvsu.mipsunit.munit.MUnit.*;

public class MaxSentinelTest {

  private void assertMax(int expected, int... values) throws Exception {
    Label array = wordData(values);
    run("max_sentinel", array);
    Assert.assertEquals(expected, get(v0));
    Assert.assertArrayEquals(values, getWords(array, 0, values.length));
  }

  @Test
  public void max_sentinel_example() throws Exception {
    assertMax(9, 4, 6, 9, 2, 3, -1, 7, 12);
  }

  @Test
  public void max_sentinel_empty() throws Exception {
    assertMax(0, -1);
    assertMax(0, -7, 1000);
    assertMax(0, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void max_sentinel_singleElement() throws Exception {
    assertMax(1, 1, -1);
    assertMax(7, 7, -1);
  }

  @Test
  public void max_sentinel_eachPosition() throws Exception {
    assertMax(9, 9, 4, 2, -1);
    assertMax(9, 4, 9, 2, -1);
    assertMax(9, 4, 2, 9, -1);
  }

  @Test
  public void max_sentinel_ties() throws Exception {
    assertMax(7, 7, 7, 7, -1);
    assertMax(9, 9, 2, 9, -1);
  }

  @Test
  public void max_sentinel_anyNegativeTerminates() throws Exception {
    assertMax(6, 4, 6, -13, 1000);
    assertMax(6, 4, 6, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void max_sentinel_ignoresFollowingElements() throws Exception {
    assertMax(1, 1, -1, Integer.MAX_VALUE);
    assertMax(7, 7, -1, 8, 9, 1000, -1);
  }

  @Test
  public void max_sentinel_integerLimits() throws Exception {
    assertMax(Integer.MAX_VALUE, Integer.MAX_VALUE, 1, -1);
    assertMax(Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MIN_VALUE);
  }

  @Test
  public void max_sentinel_longerArray() throws Exception {
    int[] values = new int[257];
    for (int index = 0; index < 256; index++) {
      values[index] = index + 1;
    }
    values[256] = -1;
    assertMax(256, values);
  }
}