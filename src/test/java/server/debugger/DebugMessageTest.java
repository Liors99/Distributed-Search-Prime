package server.debugger;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class DebugMessageTest {

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testDebugMessage() {

        DebugMessage dM = new DebugMessage("Hello");

        //Testing scientific notation result
        assertEquals(true, dM.getMessage().equals("Hello"));
        dM.setMessage("Bye");
        assertEquals(true, dM.getMessage().equals("Bye"));
    }


}
