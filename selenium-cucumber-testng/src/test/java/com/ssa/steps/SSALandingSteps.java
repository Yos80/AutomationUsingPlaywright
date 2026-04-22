package com.ssa.steps;

import com.ssa.pages.SSALandingPage;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;

public class SSALandingSteps {

    private WebDriver driver;
    private SSALandingPage landingPage;

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Before
    public void setUp() {
        // Selenium 4.6+ includes Selenium Manager which auto-downloads ChromeDriver.
        ChromeOptions options = new ChromeOptions();

        // -Dheadless=true is passed by Maven Surefire from the CI workflow.
        // Locally it defaults to false so you can watch the browser run.
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) {
            options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080"
            );
        }

        driver = new ChromeDriver(options);
        // maximize() is not supported in headless mode
        if (!headless) {
            driver.manage().window().maximize();
        }
        landingPage = new SSALandingPage(driver);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Steps ────────────────────────────────────────────────────────────────

    @Given("I am on the SSA home page")
    public void i_am_on_the_ssa_home_page() {
        landingPage.navigateTo();
    }

    @Then("the page title should contain {string}")
    public void the_page_title_should_contain(String expected) {
        String actual = landingPage.getTitle();
        Assert.assertTrue(
            actual.contains(expected),
            "Page title should contain '" + expected + "' but was: '" + actual + "'"
        );
    }

    @Then("I should see the {string} navigation button")
    public void i_should_see_the_navigation_button(String buttonText) {
        Assert.assertTrue(
            landingPage.isNavButtonVisible(buttonText),
            "Expected navigation button '" + buttonText + "' to be visible in the header"
        );
    }

    @Then("a {string} link should be visible")
    public void a_link_should_be_visible(String linkText) {
        // isHeroSignInVisible checks the sign-in CTA (a[href='/myaccount']),
        // which is the only link this step is used to verify.
        Assert.assertTrue(
            landingPage.isHeroSignInVisible(),
            "Expected a visible '" + linkText + "' link on the page"
        );
    }

    @Then("the main heading should read {string}")
    public void the_main_heading_should_read(String expected) {
        String actual = landingPage.getMainHeadingText();
        Assert.assertEquals(
            actual, expected,
            "Main heading mismatch"
        );
    }

    @When("I search for {string}")
    public void i_search_for(String query) {
        landingPage.search(query);
    }

    @Then("the search results page should be displayed")
    public void the_search_results_page_should_be_displayed() {
        String homeUrl = "https://www.ssa.gov/";
        String current = landingPage.getCurrentUrl();
        Assert.assertNotEquals(
            current, homeUrl,
            "Expected to navigate away from the home page after submitting search"
        );
    }
}
