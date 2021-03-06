package se.claremont.autotest.common;

import se.claremont.autotest.support.SupportMethods;

/**
 * A test set is a set of test cases
 *
 * Created by jordam on 2016-08-17.
 */
public class TestSet {
    public TestCase currentTestCase;
    public String name;
    final KnownErrorsList knownErrorsList = new KnownErrorsList();

    /**
     * Setting up a new test set instance
     */
    public TestSet(){
        TestRun.currentTestSet = this;
        name = SupportMethods.classNameAtStacktraceLevel(3);
    }

    /**
     * Known errors can be entered at a test set level, making them valid for all test cases in the test set.
     * All patterns entered must be found in a test case for the known error to match
     * @param description The text string describing the error
     * @param regexPatternsForLogRowsThatMustOccur TestCaseLog patterns to find in the test case execution testCaseLog
     */
    @SuppressWarnings("unused")
    public void addKnownError(String description, String[] regexPatternsForLogRowsThatMustOccur){
        knownErrorsList.add(new KnownError(description, regexPatternsForLogRowsThatMustOccur));
    }

    /**
     * Known errors can be entered at a test set level, making them valid for all test cases in the test set.
     * All patterns entered must be found in a test case for the known error to match
     * @param description The text string describing the error
     * @param regexPatternForLogRow TestCaseLog pattern to find in the test case execution testCaseLog
     */
    public void addKnownError(String description, String regexPatternForLogRow){
        knownErrorsList.add(new KnownError(description, regexPatternForLogRow));
    }

    /**
     * Procedures common for all test cases
     * @param testName The name of the test, for reporting purposes.
     */
    public void startUpTestCase(String testName){
        currentTestCase = new TestCase(knownErrorsList, testName);
    }

    /**
     * Closes down test case execution.
     * Test case tear down procedure at the test set level
      */
    protected void wrapUpTestCase(){
        currentTestCase.report();
    }
}
