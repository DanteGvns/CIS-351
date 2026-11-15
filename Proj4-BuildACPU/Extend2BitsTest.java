import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

public class Extend2BitsTest {
    @Test
    public void appendsTwoLowZeroBits() {
        long[] inputs = {0, 1, 0x123456, 0x2000000, 0x3FFFFFF, 0};
        for (long input : inputs) {
            setPinUnsigned("Input26", input);
            run();
            Assert.assertEquals(String.format("Input26=0x%07X", input),
                    input << 2, readPinUnsigned("Output28"));
        }
    }
}
