package edu.memphis.iis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest  extends TestCase {
    public AppTest(String testName) {
        super( testName );
    }

    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    // Attention: you probably need to tweak your IDE's memory settings to
    // get these tests to run (I use 2GB min and 4GB max).
    public void testApp() {
        MatePlusProcessor processor = new MatePlusProcessor();
        try {
            processor.defaultRun();
            assertTrue(true);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
