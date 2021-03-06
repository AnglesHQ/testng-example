package com.qa.utils;

import com.github.angleshq.angles.AnglesReporter;
import com.github.angleshq.angles.api.models.build.Artifact;
import com.github.angleshq.angles.api.models.screenshot.ImageCompareResponse;
import com.github.angleshq.angles.api.models.screenshot.Screenshot;
import com.github.angleshq.angles.api.models.screenshot.ScreenshotDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.github.angleshq.angles.AnglesReporterInterface;

import java.util.Properties;

public class Reporter {

    private static AnglesReporterInterface anglesReporter;
    private static final Logger logger = LogManager.getLogger(Reporter.class);
    private static Properties anglesProperties;
    private static Boolean anglesEnabled;

    static {
        anglesProperties = PropertiesHelper.loadPropertiesFile("/angles.properties");
        anglesEnabled = Boolean.valueOf(anglesProperties.getProperty("angles_enabled", "false"));

        if (anglesEnabled) {
            anglesReporter = AnglesReporter.getInstance(anglesProperties.getProperty("angles_host") + "/rest/api/v1.0/");

            anglesReporter.startBuild(anglesProperties.getProperty("angles_run_name", "Test Run"),
                    anglesProperties.getProperty("angles_environment"),
                    anglesProperties.getProperty("angles_team"),
                    anglesProperties.getProperty("angles_component"));

            // upload artifacts, here you could make your own calls to determine versions of SUT.
            Artifact[] artifacts = new Artifact[]{
                    new Artifact("com.example", "angles-ui", "1.0.0-BETA4"),
                    new Artifact("com.example", "angles", "1.0.0-BETA4")
            };
            anglesReporter.storeArtifacts(artifacts);
        }
    }

    public static void startAction(String action) {
        if (anglesEnabled)
            anglesReporter.startAction(action);
    }

    public static void info(String message, String screenshotId) {
        logger.info("Thread [" + Thread.currentThread().getId() + "] " + message);

        if (anglesEnabled)
            anglesReporter.info(message, screenshotId);
    }
    public static Screenshot storeScreenshot(ScreenshotDetails details) {
        if (anglesEnabled)
            return anglesReporter.storeScreenshot(details);

        return null;
    }

    public static void compareAgainstBaseline(Screenshot screenshot, Double acceptableMismatch) {
        ImageCompareResponse compareResponse = anglesReporter.compareScreenshotAgainstBaseline(screenshot.getId());
        if (compareResponse != null) {
            if (compareResponse.getMisMatchPercentage() > acceptableMismatch) {
                anglesReporter.fail("Image compare [" + screenshot.getView() + "]", "Mismatch below [" + acceptableMismatch + "]%", "Mismatch was [" + compareResponse.getMisMatchPercentage() + "]", "");
            } else {
                anglesReporter.pass("Image compare [" + screenshot.getView() + "]\"", "Mismatch below [" + acceptableMismatch + "]%", "Mismatch was [" + compareResponse.getMisMatchPercentage() + "]", "");

            }
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        if (expected.equals(actual)) {
            anglesReporter.pass("compare", expected.toString(), actual.toString(), "");
        } else {
            anglesReporter.fail("compare", expected.toString(), actual.toString(), "");
        }
        Assert.assertEquals(actual, expected);
    }
}
