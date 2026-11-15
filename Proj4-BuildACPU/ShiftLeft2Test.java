import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

public class ShiftLeft2Test {
    private void verify(long input) {
        setPinUnsigned("Input32", input);
        run();
        Assert.assertEquals(String.format("Input32=0x%08X", input),
                (input << 2) & 0xFFFFFFFFL, readPinUnsigned("Output32"));
    }

    @Test
    public void shiftsPositiveAndNegativeOffsets() {
        long[] inputs = {0, 1, 0x7FFF, 0xFFFF8000L, 0xFFFFFFFCL, 0xFFFFFFFFL, 0};
        for (long input : inputs) {
            verify(input);
        }
    }

    @Test
    public void movesEachBitAndDiscardsTopTwoBits() {
        for (int bit = 0; bit < 32; bit++) {
            verify(1L << bit);
        }
        verify(0xC0000003L);
    }
}
