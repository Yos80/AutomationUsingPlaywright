package com.ssa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for https://www.ssa.gov/
 *
 * Selector notes (verified against live DOM 2026-04-11):
 *   - Logo           : scoped to header — the same alt text also appears on the footer logo
 *   - Nav buttons    : guarded by @aria-controls to exclude the hidden close button (empty text)
 *   - Sign in link   : text-based ("Sign in") — more stable than the SSO URL fragment
 *   - Search input   : type="search" + name="query" — both confirmed in the live DOM
 *   - Main H2        : class="homepage-hero__title", text confirmed stable
 */
public class SSALandingPage extends BasePage {

    private static final String URL = "https://www.ssa.gov";

    // Header logo — scoped to <header> because the same alt text appears on the footer logo too
    private final By logo = By.cssSelector("header img[alt='Social Security Administration']");

    // Hero CTA — the sign-in link visible to desktop users in the main content area.
    // The header "Sign in" button (usa-mobile-header-button) is 0×0 px on desktop, so
    // we target the always-visible hero link instead.
    private final By heroSignIn = By.cssSelector("a[href='/myaccount']");

    // Search input — type AND name confirmed in live DOM; double-attribute match avoids accidents
    private final By searchInput = By.cssSelector("input[type='search'][name='query']");

    // Hero section heading
    private final By mainHeading = By.xpath("//main//h2[contains(., 'Your most-needed services')]");

    public SSALandingPage(WebDriver driver) {
        super(driver);
    }

    /** Open the SSA homepage and wait for the logo to confirm the page loaded. */
    public void navigateTo() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(logo));
    }

    /** Returns the browser tab title. */
    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Returns true when a <button> with the given visible text exists inside
     * the header navigation.
     */
    public boolean isNavButtonVisible(String buttonText) {
        // @aria-controls guards against the hidden close button (empty text, class usa-nav__close)
        By locator = By.xpath(
            "//header//button[@aria-controls and normalize-space()='" + buttonText + "']"
        );
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Clicks the hero sign-in CTA and waits for navigation away from the home page. */
    public void clickSignIn() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(heroSignIn));
        link.click();
        wait.until(d -> !d.getCurrentUrl().equals(URL + "/"));
    }

    /** Returns true when the hero sign-in CTA link is visible on screen. */
    public boolean isHeroSignInVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(heroSignIn)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the visible text of the main hero H2. */
    public String getMainHeadingText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(mainHeading)).getText().trim();
    }

    /**
     * Types {@code query} into the search box and clicks the submit button.
     * The form has target="_blank", so the results open in a new tab.
     * This method switches the driver to that new tab before returning,
     * so subsequent URL checks land in the right window.
     */
    public void search(String query) {
        String originalHandle = driver.getWindowHandle();

        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        input.clear();
        input.sendKeys(query);

        // The submit is <input type="submit" id="edit-submit">, not a <button>
        driver.findElement(By.id("edit-submit")).click();

        // Wait for the new tab to appear, then switch into it
        wait.until(d -> d.getWindowHandles().size() > 1);
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    /** Returns the current URL of whichever window the driver is focused on. */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void clickHeadingByText(String headingText) {
        By locator = By.xpath("//main//a[normalize-space()='" + headingText + "']");
        WebElement heading = wait.until(ExpectedConditions.elementToBeClickable(locator));
        heading.click();
        wait.until(d -> !d.getCurrentUrl().equals(URL + "/"));
    }
}
