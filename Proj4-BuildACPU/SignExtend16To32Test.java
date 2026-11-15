import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

public class SignExtend16To32Test {
    @Test
    public void preservesImmediateAndReplicatesSignBit() {
        long[] inputs = {0, 1, 0x1234, 0x7FFF, 0x8000, 0xAAAA, 0xFFFC, 0xFFFF, 0x5555, 0};
        for (long input : inputs) {
            setPinUnsigned("Imm16", input);
            run();
            long expected = ((long) (short) input) & 0xFFFFFFFFL;
            Assert.assertEquals(String.format("Imm16=0x%04X", input),
                    expected, readPinUnsigned("SignImm"));
        }
    }
}
