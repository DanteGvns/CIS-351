import java.util.Arrays;
import java.util.Random;
import org.junit.*;

import static edu.gvsu.mipsunit.munit.MUnit.*;

public class InsertInOrderLengthTest {

  private void assertInsertion(int value, int unusedWord, int... values) throws Exception {
    int[] memory = Arrays.copyOf(values, values.length + 4);
    memory[values.length] = unusedWord;
    memory[values.length + 1] = Integer.MIN_VALUE;
    memory[values.length + 2] = Integer.MAX_VALUE;
    memory[values.length + 3] = 0x13579BDF;

    int[] sorted = Arrays.copyOf(values, values.length + 1);
    sorted[values.length] = value;
    Arrays.sort(sorted);
    int[] expected = memory.clone();
    System.arraycopy(sorted, 0, expected, 0, sorted.length);

    Label array = wordData(memory);
    run("insert_in_order_length", array, values.length, value);
    Assert.assertArrayEquals("Insert " + value + " into " + Arrays.toString(values),
        expected, getWords(array, 0, memory.length));
  }

  @Test
  public void insert_in_order_length_example() throws Exception {
    assertInsertion(6, -123, 2, 5, 8, 11);
  }

  @Test
  public void insert_in_order_length_empty() throws Exception {
    assertInsertion(7, Integer.MIN_VALUE);
    assertInsertion(-7, Integer.MAX_VALUE);
    assertInsertion(0, 13);
  }

  @Test
  public void insert_in_order_length_eachPosition() throws Exception {
    for (int value : new int[] {1, 3, 6, 9, 12}) {
      assertInsertion(value, -999, 2, 5, 8, 11);
    }
  }

  @Test
  public void insert_in_order_length_singleElement() throws Exception {
    assertInsertion(3, -999, 5);
    assertInsertion(7, -999, 5);
    assertInsertion(5, -999, 5);
  }

  @Test
  public void insert_in_order_length_duplicates() throws Exception {
    assertInsertion(5, -999, 2, 5, 5, 8);
    assertInsertion(2, -999, 2, 2, 2);
    assertInsertion(-3, -999, -5, -3, -3, 0);
  }

  @Test
  public void insert_in_order_length_negativeAndMixedValues() throws Exception {
    assertInsertion(-15, 999, -20, -10, -3);
    assertInsertion(0, 999, -5, -1, 3, 9);
    assertInsertion(-8, 999, -5, 0, 7);
  }

  @Test
  public void insert_in_order_length_integerLimits() throws Exception {
    assertInsertion(Integer.MIN_VALUE, 0, -1, 0, Integer.MAX_VALUE);
    assertInsertion(Integer.MAX_VALUE, 0, Integer.MIN_VALUE, -1, 0);
    assertInsertion(0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE);
    assertInsertion(Integer.MIN_VALUE, 0, Integer.MIN_VALUE);
    assertInsertion(Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
  }

  @Test
  public void insert_in_order_length_ignoresUnusedWord() throws Exception {
    for (int unusedWord : new int[] {Integer.MIN_VALUE, -1, 0, Integer.MAX_VALUE}) {
      assertInsertion(5, unusedWord, 1, 4, 8);
    }
  }

  @Test
  public void insert_in_order_length_randomized() throws Exception {
    Random random = new Random(351);
    for (int trial = 0; trial < 100; trial++) {
      int[] values = new int[random.nextInt(65)];
      for (int index = 0; index < values.length; index++) {
        values[index] = random.nextInt(41) - 20;
      }
      Arrays.sort(values);
      assertInsertion(random.nextInt(51) - 25, random.nextInt(), values);
    }
  }
}