package services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.ITestResult;
import org.testng.annotations.*;
import systems.Log;
import systems.Prop;
import systems.SpreadsheetDownloaderGoogleDrive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Engine extends ECU {
    // Maven/ TestNG parameters will override these. Only for developer testing.
    // DEFAULT_TARGET       offline to skip downloading from Google Drive | online or whatever to allow download from Google Drive
    // DEFAULT_MODE         debug to keep browser open after test | prod or whatever to close browser after test
    // DEFAULT_BROWSER      chrome | firefox | edge | safari_macos | chrome_macos | edge_windows | docker_firefox | docker_chrome | tor | proxy | anything else will default to local machine's Chromium Flatpak instance without headless mode
    // DEFAULT_ENVIRONMENT  live | prelive | uat | qa
    // DEFAULT_DOMAIN       sts | avn | exp
    // DEFAULT_WAIT         Selenium WebDriver waits in seconds
    // DEFAULT_SLEEP        Thread sleep time in milliseconds

    private static final String DEFAULT_TARGET      = "offline"; // offline or online to whether download excels form Google Drive or not
    private static final String DEFAULT_MODE        = "debug";   // will make the browser NOT close after a test. check DEFAULT_BROWSER before using: debug
    private static final String DEFAULT_BROWSER     = "gecko_esr";  // gecko_esr for firefox esr for debugging, chromium for chrome debugging
    private static final String DEFAULT_ENVIRONMENT = "prelive"; // test environment live training prelive uat qa
    private static final String DEFAULT_DOMAIN      = "sts";     // test domain sts avn exp

    protected String log_in_email;
    protected String log_in_password;
    protected String sign_up_password;

    protected DateTimeFormatter ss = DateTimeFormatter.ofPattern("uuuu-MM-dd-HH-mm-ss");

    @Parameters({"testng_environment", "testng_domain", "testng_target", "testng_browser", "testng_mode"})
    @BeforeSuite()
    public void delete_test_reports_and_screenshots_of_previous_runs(@Optional(DEFAULT_ENVIRONMENT) String testng_environment,
                                                                     @Optional(DEFAULT_DOMAIN) String testng_domain,
                                                                     @Optional(DEFAULT_TARGET) String testng_target,
                                                                     @Optional(DEFAULT_BROWSER) String testng_browser,
                                                                     @Optional(DEFAULT_MODE) String testng_mode) throws Exception {

        if (testng_target.equalsIgnoreCase("online")) {
            delete_html();
            delete_excel();
            delete_csv();
            delete_png();

            Log.warn("[DATA PROVIDER SYNC] Requesting downloads from from Google Drive");
            SpreadsheetDownloaderGoogleDrive.initMethod(Prop.web().getProperty("STS_GSHEET"), Prop.web().getProperty("sts_excel"));
            SpreadsheetDownloaderGoogleDrive.initMethod(Prop.web().getProperty("AVN_GSHEET"), Prop.web().getProperty("avn_excel"));
            SpreadsheetDownloaderGoogleDrive.initMethod(Prop.web().getProperty("EXP_GSHEET"), Prop.web().getProperty("exp_excel"));
            SpreadsheetDownloaderGoogleDrive.initMethod(Prop.web().getProperty("STS_MY_GSHEET"), Prop.web().getProperty("sts_my_excel"));
            SpreadsheetDownloaderGoogleDrive.initMethod(Prop.web().getProperty("AVN_MY_GSHEET"), Prop.web().getProperty("avn_my_excel"));
        }
        else {
            delete_html();
            delete_csv();
            delete_png();

            Log.warn("[DATA PROVIDER SYNC] Using local resources folder");
        }

        WebDriverManager.firefoxdriver().setup();
        WebDriverManager.chromedriver().setup();
        WebDriverManager.edgedriver().setup();

        startReport();
    }

    @Parameters({"testng_environment", "testng_domain", "testng_target", "testng_browser", "testng_mode"})
    @BeforeClass()
    public void configure_environment_variables(@Optional(DEFAULT_ENVIRONMENT) String testng_environment,
                                                @Optional(DEFAULT_DOMAIN) String testng_domain,
                                                @Optional(DEFAULT_TARGET) String testng_target,
                                                @Optional(DEFAULT_BROWSER) String testng_browser,
                                                @Optional(DEFAULT_MODE) String testng_mode) {

        String maven_domain = System.getProperty("domain");
        if (maven_domain.equalsIgnoreCase("fallback") || maven_domain.equalsIgnoreCase("auto")) {
            select_domain(testng_domain, testng_target);
        }
        else {
            testng_domain = System.getProperty("domain");
            select_domain(testng_domain, testng_target);
        }

        String maven_environment = System.getProperty("environment");
        if (maven_environment.equalsIgnoreCase("fallback") || maven_environment.equalsIgnoreCase("auto")) {
            select_environment(testng_environment);
        }
        else {
            testng_environment = System.getProperty("environment");
            select_environment(testng_environment);
        }
    }

    @Parameters({"testng_environment", "testng_domain", "testng_target", "testng_browser", "testng_mode"})
    @BeforeMethod
    public void initiate_browser_instance(@Optional(DEFAULT_ENVIRONMENT) String testng_environment,
                                          @Optional(DEFAULT_DOMAIN) String testng_domain,
                                          @Optional(DEFAULT_TARGET) String testng_target,
                                          @Optional(DEFAULT_BROWSER) String testng_browser,
                                          @Optional(DEFAULT_MODE) String testng_mode) throws MalformedURLException {
        sign_up_password = Prop.generic().getProperty("PASSWORD");

        String maven_browser = System.getProperty("browser");
        if (maven_browser.equalsIgnoreCase("fallback") || maven_browser.equalsIgnoreCase("auto")) {
            select_browser(testng_browser);
        }
        else {
            testng_browser = System.getProperty("environment");
            select_browser(testng_browser);
        }

        configure_selected_browser();
        initiate_webdriver_session();
    }

    @Parameters({"testng_environment", "testng_domain", "testng_target", "testng_browser", "testng_mode"})
    @AfterMethod
    public void teardown(ITestResult result,
                         @Optional(DEFAULT_ENVIRONMENT) String testng_environment,
                         @Optional(DEFAULT_DOMAIN) String testng_domain,
                         @Optional(DEFAULT_TARGET) String testng_target,
                         @Optional(DEFAULT_BROWSER) String testng_browser,
                         @Optional(DEFAULT_MODE) String testng_mode) throws IOException {

        if (result.getStatus() == ITestResult.FAILURE) {
            LocalDateTime now = LocalDateTime.now();
            take_screenshot_full(ss.format(now));
            Log.fatal("[FAILURE] FAILED TEST: " + result.getMethod().getMethodName() + " of " + result.getTestClass().getName());
        }

        if (testng_mode.equalsIgnoreCase("debug")) {
            Log.warn("END OF TEST. BROWSER WILL NOT CLOSE AUTOMATICALLY.");
        }
        else {
            if (driver != null) {
                Log.warn("[BROWSER] SESSION ENDING.");
                driver.quit();
            }
        }
    }

    @AfterSuite
    public void publish_report() {
        extent.flush();
    }

}
