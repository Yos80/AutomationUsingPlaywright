# Selenium + Cucumber + TestNG — SSA.gov Test Suite

Behavior-driven tests for [https://www.ssa.gov](https://www.ssa.gov) using Java 17, Selenium WebDriver, Cucumber (Gherkin), and TestNG.

---

## Tech stack

| Layer | Library | Version |
|---|---|---|
| Browser automation | Selenium WebDriver | 4.20.0 |
| Driver management | Selenium Manager (built-in) | — |
| BDD / Gherkin | Cucumber Java | 7.17.0 |
| Test runner | TestNG via cucumber-testng | 7.17.0 / 7.10.2 |
| Build tool | Maven | 3.9.x |
| Java | JDK 17+ | — |

---

## Project layout

```
selenium-cucumber-testng/
├── pom.xml
└── src/
    ├── main/java/com/ssa/pages/
    │   ├── BasePage.java          # Shared WebDriver + WebDriverWait
    │   └── SSALandingPage.java    # Page Object (selectors + actions)
    └── test/
        ├── java/com/ssa/
        │   ├── runner/
        │   │   └── TestRunner.java      # Cucumber ↔ TestNG wiring
        │   └── steps/
        │       └── SSALandingSteps.java # Gherkin → Selenium step definitions
        └── resources/features/
            └── ssa_landing.feature      # BDD scenarios in plain English
```

---

## Scenarios

```gherkin
Feature: SSA Landing Page

  Scenario: Page title reflects the Social Security Administration
  Scenario: Primary navigation buttons are visible
  Scenario: Sign-in call to action is visible on the page
  Scenario: Main hero heading is displayed
  Scenario: Searching for a topic navigates to results
```

---

## Prerequisites

| Tool | Install |
|---|---|
| JDK 17+ | [Temurin](https://adoptium.net) or any distribution |
| Maven 3.9+ | `~/maven/bin/mvn` or system Maven |
| Google Chrome | Any recent version — Selenium Manager auto-downloads the matching ChromeDriver |

---

## Run locally

```bash
cd selenium-cucumber-testng

# Headed — Chrome opens visibly
mvn test

# Headless — same as CI
mvn test -Dheadless=true
```

Reports are written to `target/cucumber-reports/`:
- `cucumber.html` — human-readable, open in any browser
- `cucumber.json` — machine-readable, used by reporting dashboards

---

## Selector strategy

All locators were verified against the live DOM (April 2026).

| Element | Selector | Why |
|---|---|---|
| Header logo | `header img[alt='Social Security Administration']` | Scoped to `<header>` — same alt text also appears on the footer logo |
| Nav buttons | `//header//button[@aria-controls and normalize-space()='…']` | `@aria-controls` excludes the hidden close button (empty text) |
| Sign-in CTA | `a[href='/myaccount']` | The mobile header "Sign in" is 0×0 px on desktop; this hero link is always visible |
| Search input | `input[type='search'][name='query']` | Two confirmed attributes anchor the selector |
| Search submit | `#edit-submit` | `<input type="submit">`, not a `<button>` — confirmed from live DOM |
| Hero H2 | `//main//h2[contains(., 'Your most-needed services')]` | Partial text match, scoped to `<main>` |

### Search tab handling

The SSA search form uses `target="_blank"`, so results open in a new browser tab. After submitting, the driver automatically switches to the new tab before asserting the URL.

---

## CI — GitHub Actions

The workflow lives at [.github/workflows/selenium-tests.yml](../.github/workflows/selenium-tests.yml).

It triggers on every push or pull request that touches files inside `selenium-cucumber-testng/`. It runs on `ubuntu-latest` (Chrome pre-installed) with `mvn test -Dheadless=true`. The Cucumber HTML and JSON reports are uploaded as a downloadable artifact on every run, including failures.

```
Push / PR
  └── ubuntu-latest
        ├── actions/checkout@v4
        ├── actions/setup-java@v4  (Java 17, Maven cache)
        ├── mvn test -Dheadless=true
        └── Upload cucumber-reports/ artifact (30-day retention)
```
