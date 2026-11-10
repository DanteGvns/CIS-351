import java.util.Arrays;
import java.util.Random;
import org.junit.*;

import static edu.gvsu.mipsunit.munit.MUnit.*;

public class InsertInOrderSentinelTest {

  private void assertInsertion(int value, int sentinel, int unusedWord, int... values)
      throws Exception {
    int[] memory = Arrays.copyOf(values, values.length + 5);
    memory[values.length] = sentinel;
    memory[values.length + 1] = unusedWord;
    memory[values.length + 2] = Integer.MAX_VALUE;
    memory[values.length + 3] = Integer.MIN_VALUE;
    memory[values.length + 4] = 0x13579BDF;

    int[] sorted = Arrays.copyOf(values, values.length + 1);
    sorted[values.length] = value;
    Arrays.sort(sorted);
    int[] expected = memory.clone();
    System.arraycopy(sorted, 0, expected, 0, sorted.length);
    expected[values.length + 1] = sentinel;

    Label array = wordData(memory);
    run("insert_in_order_sentinel", array, value);
    Assert.assertArrayEquals("Insert " + value + " into " + Arrays.toString(values)
        + " with sentinel " + sentinel, expected, getWords(array, 0, memory.length));
  }

  @Test
  public void insert_in_order_sentinel_example() throws Exception {
    assertInsertion(6, -1, 999, 2, 5, 8, 11);
  }

  @Test
  public void insert_in_order_sentinel_empty() throws Exception {
    assertInsertion(7, -1, 0);
    assertInsertion(1, -7, Integer.MIN_VALUE);
    assertInsertion(Integer.MAX_VALUE, Integer.MIN_VALUE, 999);
  }

  @Test
  public void insert_in_order_sentinel_eachPosition() throws Exception {
    for (int value : new int[] {1, 3, 6, 9, 12}) {
      assertInsertion(value, -7, 999, 2, 5, 8, 11);
    }
  }

  @Test
  public void insert_in_order_sentinel_singleElement() throws Exception {
    assertInsertion(3, -1, 999, 5);
    assertInsertion(7, -1, 999, 5);
    assertInsertion(5, -1, 999, 5);
  }

  @Test
  public void insert_in_order_sentinel_duplicates() throws Exception {
    assertInsertion(5, -7, 999, 2, 5, 5, 8);
    assertInsertion(2, -7, 999, 2, 2, 2);
    assertInsertion(8, -7, 999, 2, 8, 8);
  }

  @Test
  public void insert_in_order_sentinel_preservesAnyNegativeSentinel() throws Exception {
    for (int sentinel : new int[] {-1, -7, -13, Integer.MIN_VALUE}) {
      assertInsertion(1, sentinel, 999, 2, 5, 8);
      assertInsertion(6, sentinel, 999, 2, 5, 8);
      assertInsertion(9, sentinel, 999, 2, 5, 8);
    }
  }

  @Test
  public void insert_in_order_sentinel_ignoresUnusedWord() throws Exception {
    for (int unusedWord : new int[] {Integer.MIN_VALUE, -1, 0, 7, Integer.MAX_VALUE}) {
      assertInsertion(5, -7, unusedWord, 1, 4, 8);
    }
  }

  @Test
  public void insert_in_order_sentinel_integerLimits() throws Exception {
    assertInsertion(Integer.MAX_VALUE, -1, 0, 1, 2, Integer.MAX_VALUE - 1);
    assertInsertion(1, Integer.MIN_VALUE, 0, Integer.MAX_VALUE);
    assertInsertion(Integer.MAX_VALUE, -7, 0, Integer.MAX_VALUE);
  }

  @Test
  public void insert_in_order_sentinel_randomized() throws Exception {
    Random random = new Random(352);
    for (int trial = 0; trial < 100; trial++) {
      int[] values = new int[random.nextInt(65)];
      for (int index = 0; index < values.length; index++) {
        values[index] = random.nextInt(41) + 1;
      }
      Arrays.sort(values);
      int sentinel = -1 - random.nextInt(Integer.MAX_VALUE);
      assertInsertion(random.nextInt(51) + 1, sentinel, random.nextInt(), values);
    }
  }
}