package org.jenkinsci.plugins.slacknotifier;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CucumberResultTest {

    @Test
    public void canGenerateHeader() {
        String header = successfulResult().toHeader("test-job", 1, "http://localhost:8080/", "http://localhost:8080/job/test-job/1/");
        assertNotNull(header);
        assertTrue(header.contains("Features: 1"));
        assertTrue(header.contains("Scenarios: 1"));
        assertTrue(header.contains("Build: <http://localhost:8080/job/test-job/1/cucumber-html-reports/|1>"));
    }

    private CucumberResult successfulResult() {
        return new CucumberResult(Collections.singletonList(new FeatureResult("Dummy Test", 100, 1, 0,"Dummy Test")), 1, 100);
    }
}
