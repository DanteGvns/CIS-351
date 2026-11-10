import org.junit.*;

import static edu.gvsu.mipsunit.munit.MUnit.Register.*;
import static edu.gvsu.mipsunit.munit.MUnit.*;

public class IntMaxTest {

  /******************************************************************
   *
   * Test intMax
   *
   *****************************************************************/

  @Test
  public void intMax_ascending() {
    run("intMax", 5, 6, 7);
    Assert.assertEquals(7, get(v0));
  }

  @Test
  public void intMax_allOrderings() {
    int[][] cases = {
        {1, 2, 3}, {1, 3, 2}, {2, 1, 3},
        {2, 3, 1}, {3, 1, 2}, {3, 2, 1}
    };
    for (int[] values : cases) {
      run("intMax", values[0], values[1], values[2]);
      Assert.assertEquals(3, get(v0));
    }
  }

  @Test
  public void intMax_ties() {
    int[][] cases = {
        {7, 7, 2}, {7, 2, 7}, {2, 7, 7}, {7, 7, 7},
        {2, 2, 7}, {2, 7, 2}, {7, 2, 2}
    };
    for (int[] values : cases) {
      run("intMax", values[0], values[1], values[2]);
      Assert.assertEquals(7, get(v0));
    }
  }

  @Test
  public void intMax_negativeAndZero() {
    int[][] cases = {
        {-1, -2, -3, -1}, {-3, -1, -2, -1}, {-3, -2, -1, -1},
        {0, 0, 0, 0}, {-1, 0, -2, 0}, {-1, 2, 0, 2},
        {-7, -7, -7, -7}
    };
    for (int[] values : cases) {
      run("intMax", values[0], values[1], values[2]);
      Assert.assertEquals(values[3], get(v0));
    }
  }

  @Test
  public void intMax_integerLimits() {
    int[][] cases = {
        {Integer.MIN_VALUE, 0, Integer.MAX_VALUE, Integer.MAX_VALUE},
        {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, Integer.MAX_VALUE},
        {0, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE},
        {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE},
        {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE},
        {Integer.MIN_VALUE, Integer.MIN_VALUE + 1, -1, -1}
    };
    for (int[] values : cases) {
      run("intMax", values[0], values[1], values[2]);
      Assert.assertEquals(values[3], get(v0));
    }
  }

} // end class
