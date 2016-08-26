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

    public void testApp() {
        MatePlusProcessor processor = new MatePlusProcessor();
        try {
            processor.defaultRun();
            assertTrue(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
