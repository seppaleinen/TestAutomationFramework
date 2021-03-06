package se.claremont.autotest.common;

import java.util.ArrayList;

/**
 * Created by jordam on 2016-09-19.
 */
public class TestRunReporterFactory {
    private ArrayList<TestRunReporter> reporters = new ArrayList<>();

    public TestRunReporterFactory(){
        reporters.add(new TestRunReporterHtmlSummaryReportFile());
        if(TestRun.settings.getValue(Settings.SettingParameters.EMAIL_SERVER_ADDRESS) != null){
            reporters.add(new TestRunReporterEmailReport());
        }
    }


    public void addTestRunReporter(TestRunReporter testRunReporter){
        reporters.add(testRunReporter);
    }

    public void evaluateTestCase(TestCase testCase){
        for(TestRunReporter testRunReporter : reporters){
            testRunReporter.evaluateTestCase(testCase);
        }
    }

    public void evaluateTestSet(TestSet testSet){
        for(TestRunReporter testRunReporter : reporters){
            testRunReporter.evaluateTestSet(testSet);
        }
    }

    public void report(){
        reporters.forEach(TestRunReporter::report);
    }
}
