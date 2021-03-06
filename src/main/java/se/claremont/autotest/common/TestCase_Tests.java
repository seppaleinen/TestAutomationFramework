package se.claremont.autotest.common;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * Tests for the TestCase class
 *
 * Created by jordam on 2016-08-30.
 */
public class TestCase_Tests {

    @Test
    public void matchOfSeveralStringsInitial(){
        TestCase testCase = new TestCase(new KnownErrorsList(), "TestCase_Tests");
        String[] patterns = {"Pattern1", "Pattern2"};
        testCase.addKnownError("Description", patterns);
        Assert.assertTrue("Test case status expected to be '" + TestCase.ResultStatus.UNEVALUATED.toString() + "', but was '" + testCase.resultStatus.toString() + "'.", testCase.resultStatus.equals(TestCase.ResultStatus.UNEVALUATED));
    }

    @Test
    public void matchOfSeveralStringsNoError(){
        TestCase testCase = new TestCase(new KnownErrorsList(), "TestCase_Tests");
        String[] patterns = {"Pattern1", "Pattern2"};
        testCase.addKnownError("Description", patterns);
        testCase.testCaseLog.log(LogLevel.EXECUTED, "Pattern0");
        testCase.evaluateResultStatus();
        Assert.assertTrue("Test case status expected to be '" + TestCase.ResultStatus.PASSED.toString() + "', but was '" + testCase.resultStatus.toString() + "'.", testCase.resultStatus.equals(TestCase.ResultStatus.PASSED));
    }

    @Test
    public void matchOfSeveralStringsMatch(){
        TestCase testCase = new TestCase(new KnownErrorsList(), "TestCase_Tests");
        String[] patterns = {"Pattern1", "Pattern2"};
        testCase.addKnownError("Description", patterns);
        testCase.testCaseLog.log(LogLevel.VERIFICATION_PROBLEM, "Pattern1");
        testCase.testCaseLog.log(LogLevel.VERIFICATION_PROBLEM, "Pattern2");
        testCase.evaluateResultStatus();
        Assert.assertTrue("Test case status expected to be '" + TestCase.ResultStatus.FAILED_WITH_ONLY_KNOWN_ERRORS.toString() + "', but was '" + testCase.resultStatus.toString() + "'.", testCase.resultStatus.equals(TestCase.ResultStatus.FAILED_WITH_ONLY_KNOWN_ERRORS));
    }

    @Test
    public void matchOfSeveralStringsBothMatchAndOtherErrors(){
        TestCase testCase = new TestCase(new KnownErrorsList(), "TestCase_Tests");
        String[] patterns = {"Pattern1", "Pattern2"};
        testCase.addKnownError("Description", patterns);
        testCase.testCaseLog.log(LogLevel.VERIFICATION_PROBLEM, "Pattern1");
        testCase.testCaseLog.log(LogLevel.VERIFICATION_PROBLEM, "Pattern2");
        testCase.testCaseLog.log(LogLevel.VERIFICATION_PROBLEM, "Pattern3");
        testCase.evaluateResultStatus();
        Assert.assertTrue("Test case status expected to be '" + TestCase.ResultStatus.FAILED_WITH_BOTH_NEW_AND_KNOWN_ERRORS.toString() + "', but was '" + testCase.resultStatus.toString() + "'.", testCase.resultStatus.equals(TestCase.ResultStatus.FAILED_WITH_BOTH_NEW_AND_KNOWN_ERRORS));
    }

    @Test
    public void matchOfSeveralStringsOnlyNewErrors(){
        TestCase testCase = new TestCase(new KnownErrorsList(), "TestCase_Tests");
        String[] patterns = {"Pattern1", "Pattern2"};
        testCase.addKnownError("Description", patterns);
        testCase.testCaseLog.log(LogLevel.EXECUTED, "Pattern0");
        testCase.testCaseLog.log(LogLevel.VERIFICATION_PROBLEM, "Pattern2");
        testCase.testCaseLog.log(LogLevel.VERIFICATION_PROBLEM, "Pattern3");
        testCase.evaluateResultStatus();
        Assert.assertTrue("Test case status expected to be '" + TestCase.ResultStatus.FAILED_WITH_ONLY_NEW_ERRORS.toString() + "', but was '" + testCase.resultStatus.toString() + "'.", testCase.resultStatus.equals(TestCase.ResultStatus.FAILED_WITH_ONLY_NEW_ERRORS));
    }

    @Test
    public void testCaseDataAddingAndReading(){
        TestCase testCase = new TestCase(new KnownErrorsList(), "TestCase_Tests");
        testCase.addTestCaseData("Parameter name", "Parameter DATA");
        Assert.assertTrue("Test case DATA was expected to be 'Parameter DATA' but was '"  + testCase.valueForFirstMatchForTestCaseDataParameter("Parameter name"), testCase.valueForFirstMatchForTestCaseDataParameter("Parameter name").equals("Parameter DATA"));
    }

    @Test
    public void testCaseDataNoMatchIsEmptyString(){
        TestCase testCase = new TestCase(new KnownErrorsList(), "TestCase_Tests");
        testCase.addTestCaseData("Parameter name", "Parameter DATA");
        Assert.assertTrue(testCase.valueForFirstMatchForTestCaseDataParameter("Wrong Parameter name").equals(""));
    }

    @Test
    public void testCaseDataMultipleMatches(){
        TestCase testCase = new TestCase(new KnownErrorsList(), "TestCase_Tests");
        testCase.addTestCaseData("Parameter name", "Parameter DATA");
        testCase.addTestCaseData("Parameter name", "Parameter DATA 2");
        Assert.assertTrue(testCase.valueForFirstMatchForTestCaseDataParameter("Parameter name").equals("Parameter DATA"));
    }

    @Test
    public void isSameAsTest(){
        TestCase testCase = new TestCase(new KnownErrorsList(), "isSameAsTest");
        TestCase testCase2 = new TestCase(new KnownErrorsList(), "isSameAsTest");
        Assert.assertTrue(testCase.isSameAs(testCase));
        Assert.assertFalse(testCase.isSameAs(testCase2));
    }

    //Used for printing test
    @Test
    @Ignore
    public void testCaseEvaluationStatusPrintoutCheck(){
        KnownErrorsList knownErrorsList = new KnownErrorsList();
        knownErrorsList.add(new KnownError("TEST", ".*Text.*"));
        TestCase testCase = new TestCase(knownErrorsList, "dummy");

        //Passed
        testCase.log(LogLevel.VERIFICATION_PASSED, "Text");
        testCase.evaluateResultStatus();

        //Only new error
        testCase.log(LogLevel.FRAMEWORK_ERROR, "Other error");
        testCase.evaluateResultStatus();

        //Both new and known errors
        testCase.log(LogLevel.VERIFICATION_FAILED, "Text");
        testCase.evaluateResultStatus();

        knownErrorsList.add(new KnownError("TEST2", ".*Other error.*"));
        testCase.evaluateResultStatus();
    }

}
