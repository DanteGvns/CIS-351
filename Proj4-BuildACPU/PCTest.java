import java.lang.reflect.Field;
import java.util.BitSet;
import edu.mtu.cs.jls.Circuit;
import edu.mtu.cs.jls.JLSInfo;
import edu.mtu.cs.jls.elem.Element;
import edu.mtu.cs.jls.elem.InputPin;
import edu.mtu.cs.jls.elem.TestGen;
import edu.mtu.cs.jls.sim.SimEvent;
import edu.mtu.cs.jls.sim.Simulator;
import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

public class PCTest {
    @Test
    public void capturesOnlyOnFallingEdges() throws Exception {
        Field circuitField = Simulator.class.getDeclaredField("circuit");
        circuitField.setAccessible(true);
        Circuit circuit = (Circuit) circuitField.get(JLSInfo.sim);
        InputPin nextPC = null;
        InputPin clock = null;
        for (Element element : circuit.getElements()) {
            if (element instanceof InputPin) {
                InputPin pin = (InputPin) element;
                if (pin.getName().equals("NextPC")) nextPC = pin;
                if (pin.getName().equals("Clock")) clock = pin;
            }
        }
        Assert.assertNotNull(nextPC);
        Assert.assertNotNull(clock);
        final InputPin dataPin = nextPC;
        final InputPin clockPin = clock;
        TestGen stimulus = new TestGen(circuit) {
            @Override
            public void initSim(Simulator simulator) {
                post(simulator, dataPin, 10, 4);
                post(simulator, clockPin, 100, 1);
                post(simulator, clockPin, 200, 0);
                post(simulator, dataPin, 300, 0x00400020);
                post(simulator, clockPin, 400, 1);
                post(simulator, clockPin, 500, 0);
                post(simulator, dataPin, 600, 0xFFFFFFFCL);
                post(simulator, clockPin, 700, 1);
                post(simulator, clockPin, 800, 0);
            }

            private void post(Simulator simulator, InputPin pin, long time, long value) {
                simulator.post(new SimEvent(time, pin, BitSet.valueOf(new long[] {value})));
            }
        };
        circuit.addElement(stimulus);
        Field limitField = Simulator.class.getDeclaredField("maxTime");
        limitField.setAccessible(true);
        long originalLimit = limitField.getLong(JLSInfo.sim);
        long[] times = {50, 150, 275, 350, 450, 575, 750, 875};
        long[] expected = {0, 0, 4, 4, 4, 0x00400020, 0x00400020, 0xFFFFFFFCL};
        try {
            for (int i = 0; i < times.length; i++) {
                reset();
                JLSInfo.sim.setTimeLimit(times[i]);
                run();
                Assert.assertEquals("Address at time " + times[i],
                        expected[i], readPinUnsigned("Address"));
            }
        } finally {
            circuit.remove(stimulus);
            JLSInfo.sim.setTimeLimit(originalLimit);
        }
    }
}
