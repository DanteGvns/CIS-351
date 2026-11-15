import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

public class Add4Test {
    @Test
    public void addsFourWith32BitWraparound() {
        long[] inputs = {0, 1, 4, 0x00400000, 0x7FFFFFFF, 0xFFFFFFFBL,
                0xFFFFFFFCL, 0xFFFFFFFFL, 0};
        for (long input : inputs) {
            setPinUnsigned("Input32", input);
            run();
            Assert.assertEquals(String.format("Input32=0x%08X", input),
                    (input + 4) & 0xFFFFFFFFL, readPinUnsigned("Output32"));
        }
    }
}
