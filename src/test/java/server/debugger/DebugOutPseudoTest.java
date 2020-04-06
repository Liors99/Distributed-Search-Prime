package server.debugger;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class DebugOutPseudoTest {

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    /**
     * please view output to check it works
     */
    @Test
    void testDebugMessage() {
        DebugOut dOut = new DebugOut();
        dOut.start();
        DebugMessage dM = new DebugMessage("Hello");
        System.out.println("See console out!");
        dOut.addMessageToQueue(dM);

    }


}
