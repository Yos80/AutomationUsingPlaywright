package com.ssa.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * TestNG entry point for the Cucumber suite.
 *
 * Maven Surefire is configured in pom.xml to include TestRunner.java,
 * so running "mvn test" is all that is needed to execute every scenario
 * in src/test/resources/features/.
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue     = "com.ssa.steps",
    plugin   = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json"
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
    // AbstractTestNGCucumberTests wires Cucumber into the TestNG lifecycle.
    // No additional code is required here.
}
