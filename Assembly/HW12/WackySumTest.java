import org.junit.*;
import java.util.Random;

import static edu.gvsu.mipsunit.munit.MUnit.Register.*;
import static edu.gvsu.mipsunit.munit.MUnit.*;

// Compile alongside Demo.java, which supplies the assignment's reference results.
// Both wackySum and combineFour must be declared .globl in the assembly.
// Nonempty loops with c <= 0 are excluded because the Java reference does not terminate.
public class WackySumTest {

    @Before
    public void before() {
    set(v0, 1337);
    }

    /******************************************************************
     *
     * Test wackySum
     *
     *****************************************************************/

    @Test 
    public void wacky_3_7_2() {
    run("wackySum", 3, 7, 2);
    Assert.assertEquals(27, get(v0));
    }

    @Test 
    public void wacky_21_26_1() {
    run("wackySum", 21, 26, 1);
    Assert.assertEquals(336, get(v0));
    }

    @Test 
    public void wacky_33_42_2() {
    run("wackySum", 33, 42, 2);
    Assert.assertEquals(285, get(v0));
    }

    private void checkWacky(int a, int b, int c) {
        set(v0, 1337);
        run("wackySum", a, b, c);
        Assert.assertEquals("wackySum(" + a + ", " + b + ", " + c + ")",
                Demo.wackySum(a, b, c), get(v0));
    }

    private void checkCombine(int a, int b, int c, int d) {
        set(v0, 1337);
        run("combineFour", a, b, c, d);
        Assert.assertEquals("combineFour(" + a + ", " + b + ", " + c + ", " + d + ")",
                Demo.combineFour(a, b, c, d), get(v0));
    }

    @Test
    public void wacky_emptyRanges() {
        int[][] cases = {
            {1, 0, 1}, {5, 3, 2}, {-1, -5, 1}, {0, -1, 3},
            {100, 10, 1}, {-10, -20, 4}, {1, 0, 0}, {1, 0, -1}
        };
        for (int[] values : cases) {
            checkWacky(values[0], values[1], values[2]);
        }
    }

    @Test
    public void wacky_singleIterations() {
        for (int i = -6; i <= 6; i++) {
            checkWacky(i, i, 1);
        }
    }

    @Test
    public void wacky_inclusiveAndOvershotBounds() {
        int[][] cases = {
            {0, 1, 1}, {1, 2, 1}, {0, 4, 2}, {0, 5, 2},
            {1, 7, 3}, {1, 8, 3}, {3, 7, 100}, {-3, 3, 6}, {-3, 2, 6}
        };
        for (int[] values : cases) {
            checkWacky(values[0], values[1], values[2]);
        }
    }

    @Test
    public void wacky_negativeAndCrossingZero() {
        int[][] cases = {
            {-10, -1, 1}, {-10, -2, 2}, {-9, -1, 2}, {-5, -1, 2},
            {-4, -2, 1}, {-3, -1, 1}, {-2, -1, 1},
            {-10, 10, 1}, {-9, 8, 2}, {-5, 5, 3}, {-1, 1, 1}, {-2, 2, 2}
        };
        for (int[] values : cases) {
            checkWacky(values[0], values[1], values[2]);
        }
    }

    @Test
    public void wacky_differentSteps() {
        for (int step : new int[] {1, 2, 3, 4, 7, 100}) {
            checkWacky(0, 20, step);
        }
    }

    @Test
    public void wacky_smallInputGrid() {
        for (int a = -3; a <= 3; a++) {
            for (int b = -3; b <= 3; b++) {
                for (int c = 1; c <= 3; c++) {
                    checkWacky(a, b, c);
                }
            }
        }
    }

    @Test
    public void wacky_deterministicRandomInputs() {
        Random random = new Random(35112);
        for (int i = 0; i < 40; i++) {
            checkWacky(random.nextInt(101) - 50,
                    random.nextInt(101) - 50, random.nextInt(10) + 1);
        }
    }

    @Test
    public void wacky_longerRanges() {
        int[][] cases = {
            {0, 100, 1}, {-100, 100, 1}, {100, 300, 3},
            {1000, 1100, 7}, {-300, -100, 4}
        };
        for (int[] values : cases) {
            checkWacky(values[0], values[1], values[2]);
        }
    }

    @Test
    public void wacky_repeatedCallsResetSum() {
        int[][] cases = {
            {3, 7, 2}, {5, 3, 1}, {-5, -1, 2},
            {0, 0, 1}, {3, 7, 2}, {21, 26, 1}
        };
        for (int[] values : cases) {
            checkWacky(values[0], values[1], values[2]);
        }
    }

    @Test
    public void combine_parityAndSignedDivision() {
        int[][] cases = {
            {0, 0, 0, 0}, {1, 0, 0, 0}, {-1, 0, 0, 0},
            {1, 2, 3, 4}, {1, 2, 3, 5}, {-1, -2, -3, -4},
            {-1, -2, -3, -5}, {10, -10, 20, -20},
            {7, -3, 2, -1}, {-7, 3, -2, 1},
            {0, 0, 0, 9}, {0, 0, 0, -9}
        };
        for (int[] values : cases) {
            checkCombine(values[0], values[1], values[2], values[3]);
        }
    }

    @Test
    public void combine_smallInputGrid() {
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                for (int c = -1; c <= 1; c++) {
                    for (int d = -1; d <= 1; d++) {
                        checkCombine(a, b, c, d);
                    }
                }
            }
        }
    }

    @Test
    public void combine_deterministicRandomInputs() {
        Random random = new Random(35104);
        for (int i = 0; i < 40; i++) {
            checkCombine(random.nextInt(2001) - 1000, random.nextInt(2001) - 1000,
                    random.nextInt(2001) - 1000, random.nextInt(2001) - 1000);
        }
    }

    @Test
    public void combine_largeValuesWithoutOverflow() {
        int[][] cases = {
            {1000000, 2000000, 3000000, 4000000},
            {-1000000, -2000000, -3000000, -4000001},
            {Integer.MAX_VALUE, 0, 0, 0}, {Integer.MIN_VALUE, 0, 0, 0},
            {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 0},
            {500000000, 500000000, 500000000, 500000000}
        };
        for (int[] values : cases) {
            checkCombine(values[0], values[1], values[2], values[3]);
        }
    }

    @Test
    public void combine_javaIntegerWraparound() {
        // Java int addition wraps instead of trapping on signed overflow.
        int[][] cases = {
            {Integer.MAX_VALUE, 1, 0, 0},
            {Integer.MIN_VALUE, -1, 0, 0},
            {Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0},
            {Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0}
        };
        for (int[] values : cases) {
            checkCombine(values[0], values[1], values[2], values[3]);
        }
    }

    private void seedSavedRegisters() {
        set(s0, 101);
        set(s1, -202);
        set(s2, 303);
        set(s3, -404);
        set(s4, 505);
        set(s5, -606);
        set(s6, 707);
        set(s7, -808);
    }

    private void checkSavedRegisters(int stackBefore) {
        Assert.assertEquals("s0", 101, get(s0));
        Assert.assertEquals("s1", -202, get(s1));
        Assert.assertEquals("s2", 303, get(s2));
        Assert.assertEquals("s3", -404, get(s3));
        Assert.assertEquals("s4", 505, get(s4));
        Assert.assertEquals("s5", -606, get(s5));
        Assert.assertEquals("s6", 707, get(s6));
        Assert.assertEquals("s7", -808, get(s7));
        Assert.assertEquals("sp", stackBefore, get(sp));
    }

    @Test
    public void wacky_preservesCallerState() {
        int[][] cases = {{3, 7, 2}, {5, 3, 1}, {-5, 5, 1}};
        for (int[] values : cases) {
            seedSavedRegisters();
            int stackBefore = get(sp);
            checkWacky(values[0], values[1], values[2]);
            checkSavedRegisters(stackBefore);
        }
    }

    @Test
    public void combine_preservesCallerState() {
        int[][] cases = {{1, 2, 3, 4}, {1, 2, 3, 5}, {-1, -2, -3, -5}};
        for (int[] values : cases) {
            seedSavedRegisters();
            int stackBefore = get(sp);
            checkCombine(values[0], values[1], values[2], values[3]);
            checkSavedRegisters(stackBefore);
        }
    }
    
} // end class


