package com.qa.tests.parallel.example_06b;

import com.qa.tests.parallel.common.BaseTest;
import com.qa.utils.Reporter;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class ThreadSafeExampleOne extends BaseTest {

    // Thread local: this variable has to be set for each thread and can only be access by that thread
    private ThreadLocal<String> testName = new ThreadLocal<>();

    @BeforeClass
    private void testBeforeClass() {
        logger.info("ThreadSafeExampleOne: @BeforeClass");
        pause();
    }

    @AfterClass
    private void testAfterClass() {
        logger.info("ThreadSafeExampleOne: @AfterClass");
        pause();
    }

    @BeforeMethod
    private void testBeforeMethod(Method method) {
        // as the before method is run by the same thread as the test we can set it here.
        testName.set(method.getName());
        logger.info("ThreadSafeExampleOne: @BeforeMethod" + "[ " + testName.get() + "]");
        pause();
    }

    @AfterMethod
    private void testAfterMethod() {
        logger.info("ThreadSafeExampleOne: @AfterMethod" + "[ " + testName.get() + "]");
        pause();
    }

    @Test
    private void ParallelInstancesOneTest1() {
        logger.info("ThreadSafeExampleOne: ParallelInstancesOneTest1" + "[ " + testName.get() + "]");
    }

    @Test
    private void ParallelInstancesOneTest2() {
        logger.info("ThreadSafeExampleOne: ParallelInstancesOneTest2" + "[ " + testName.get() + "]");
    }

    @Test
    private void ParallelInstancesOneTest3() {
        logger.info("ThreadSafeExampleOne: ParallelInstancesOneTest3" + "[ " + testName.get() + "]");
    }
}
