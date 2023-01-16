package services;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import systems.Log;
import systems.Prop;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class ECU {

    protected int ALTERNATIVE_THREAD_SLEEP = 3;    // [5] thread sleep. bad practice.
    protected int DEFAULT_THREAD_SLEEP     = 4;   // [10] thread sleep. bad practice.
    protected int ALTERNATIVE_WD_WAIT      = 1;    // [2] custom WebDriverWait used to skip actions if element not present. use only when really needed. do not change values if you don't know what you're doing.
    protected int DEFAULT_WD_WAIT          = 9;   // [15] default WebDriverWait duration.

    private static final String FIREFOX_ESR_BINARY_ARCH = "/usr/bin/firefox-esr";
    private static final String CHROMIUM_BINARY_ARCH = "/usr/bin/chromium";

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    protected ChromeOptions chromeOptions;
    protected EdgeOptions edgeOptions;
    protected FirefoxOptions firefoxOptions;

    protected String DATA_PROVIDER_WEB;
    protected String DATA_PROVIDER_HUB;
    protected String DATA_PROVIDER_MY;
    protected String URL_WEB;
    protected String URL_HUB;
    protected String URL_MY;
    protected String URL_DOC;
    protected String DB_HOST;
    protected String DB_UN;
    protected String DB_PW;
    protected String PARAM_BROWSER;
    protected String PARAM_DOMAIN;
    protected String PARAM_ENVIRONMENT;
    protected String PARAM_SCOPE;

    public ExtentSparkReporter htmlReporter;
    public static ExtentReports extent;
    public static ExtentTest extentTest;

    public void startReport() {
        htmlReporter = new ExtentSparkReporter("test-performance/extentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        htmlReporter.config().setTimelineEnabled(true);
        htmlReporter.config().setDocumentTitle("Group F Automation");
        htmlReporter.config().setReportName("Unification Automation Report");
        htmlReporter.config().setTheme(Theme.DARK);
        htmlReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

        Log.info("[EXTENT REPORTER] Initialized!");
    }

    public static void delete_png() throws IOException {
        File screenshots = new File("test-screenshots");
        File[] imageFiles = screenshots.listFiles((d,f)-> f.toLowerCase().endsWith(".png"));

        if (imageFiles != null) {
            for(File f : imageFiles) {
                if(!f.delete())
                    throw new IOException("Not able to delete png files: " + f.getAbsolutePath());
            }
        }

        Log.warn("[WORKSPACE] Removed screenshots!");
    }

    public static void delete_csv() throws IOException {
        File performance = new File("test-performance");
        File[] csvFiles = performance.listFiles((d,f)-> f.toLowerCase().endsWith(".csv"));

        if (csvFiles != null) {
            for(File f : csvFiles) {
                if(!f.delete())
                    throw new IOException("Not able to delete csv files: " + f.getAbsolutePath());
            }
        }

        Log.warn("[WORKSPACE] Removed csv results!");
    }

    public static void delete_html() throws IOException {
        File performance = new File("test-performance");
        File[] htmlFiles = performance.listFiles((d,f)-> f.toLowerCase().endsWith(".html"));

        if (htmlFiles != null) {
            for(File f : htmlFiles) {
                if(!f.delete())
                    throw new IOException("Not able to delete html files: " + f.getAbsolutePath());
            }
        }

        Log.warn("[WORKSPACE] Removed html files!");
    }

    public static void delete_excel() throws IOException {
        File data = new File("test-data");
        File[] excelFiles = data.listFiles((d,f)-> f.toLowerCase().endsWith(".xlsx"));

        if (excelFiles != null) {
            for(File f : excelFiles) {
                if(!f.delete())
                    throw new IOException("Not able to delete png files: " + f.getAbsolutePath());
            }
        }

        Log.warn("[WORKSPACE] Removed data provider excel files!");
    }

    public void select_domain(String testng_domain, String testng_target) {
        PARAM_DOMAIN = testng_domain.toLowerCase();
        Log.warn("[PARAM_DOMAIN] " + PARAM_DOMAIN);

        if (testng_target.equalsIgnoreCase("online")) {
            DATA_PROVIDER_WEB = Prop.web().getProperty(this.PARAM_DOMAIN + "_excel");           Log.warn("[DATA_PROVIDER_WEB] " + DATA_PROVIDER_WEB);
            DATA_PROVIDER_HUB = Prop.web().getProperty(this.PARAM_DOMAIN + "_excel");           Log.warn("[DATA_PROVIDER_HUB] " + DATA_PROVIDER_HUB);
            DATA_PROVIDER_MY = Prop.web().getProperty(this.PARAM_DOMAIN + "_my_excel");         Log.warn("[DATA_PROVIDER_MY] " + DATA_PROVIDER_MY);
        }
        else {
            DATA_PROVIDER_WEB = Prop.web().getProperty(this.PARAM_DOMAIN + "_excel_local");     Log.warn("[DATA_PROVIDER_WEB] " + DATA_PROVIDER_WEB);
            DATA_PROVIDER_HUB = Prop.web().getProperty(this.PARAM_DOMAIN + "_excel_local");     Log.warn("[DATA_PROVIDER_HUB] " + DATA_PROVIDER_HUB);
            DATA_PROVIDER_MY = Prop.web().getProperty(this.PARAM_DOMAIN + "_my_excel_local");   Log.warn("[DATA_PROVIDER_MY] " + DATA_PROVIDER_MY);
        }
    }

    public void select_environment(String testng_environment) {
        PARAM_ENVIRONMENT = testng_environment.toLowerCase();
        Log.warn("[PARAM_ENVIRONMENT] " + PARAM_ENVIRONMENT);

        if (PARAM_ENVIRONMENT.startsWith("qa") || PARAM_ENVIRONMENT.startsWith("uat")) {
            URL_HUB = Prop.web().getProperty(PARAM_DOMAIN + "_hub_1") + PARAM_ENVIRONMENT + Prop.web().getProperty(PARAM_DOMAIN + "_hub_2");
            URL_WEB = Prop.web().getProperty(PARAM_DOMAIN + "_web_1") + PARAM_ENVIRONMENT + Prop.web().getProperty(PARAM_DOMAIN + "_web_2");
            URL_MY = Prop.web().getProperty(PARAM_DOMAIN + "_my_1") + PARAM_ENVIRONMENT + Prop.web().getProperty(PARAM_DOMAIN + "_my_2");
            URL_DOC = Prop.web().getProperty(PARAM_DOMAIN + "_doc_1") + PARAM_ENVIRONMENT + Prop.web().getProperty(PARAM_DOMAIN + "_doc_2");

            DB_UN = PARAM_ENVIRONMENT + PARAM_DOMAIN;
            DB_PW = Prop.web().getProperty("ORACLE_GENERIC_PASSWORD");

            boolean db_host_19 = (DB_UN.equalsIgnoreCase("qa01sts")) || (DB_UN.equalsIgnoreCase("qa03sts"))
                    || (DB_UN.equalsIgnoreCase("qa07sts")) || (DB_UN.equalsIgnoreCase("qa01avn"))
                    || (DB_UN.equalsIgnoreCase("qa09avn")) || (DB_UN.equalsIgnoreCase("uat02exp"))
                    || (DB_UN.equalsIgnoreCase("uat01sts")) || (DB_UN.equalsIgnoreCase("uat02sts"))
                    || (DB_UN.equalsIgnoreCase("uat03sts")) || (DB_UN.equalsIgnoreCase("uat05sts"))
                    || (DB_UN.equalsIgnoreCase("uat08sts")) || (DB_UN.equalsIgnoreCase("uat09sts"))
                    || (DB_UN.equalsIgnoreCase("uat10sts")) || (DB_UN.equalsIgnoreCase("uat11sts"))
                    || (DB_UN.equalsIgnoreCase("uat12sts")) || (DB_UN.equalsIgnoreCase("uat01avn"))
                    || (DB_UN.equalsIgnoreCase("uat02avn")) || (DB_UN.equalsIgnoreCase("uat03avn"))
                    || (DB_UN.equalsIgnoreCase("uat08avn")) || (DB_UN.equalsIgnoreCase("uat09avn"))
                    || (DB_UN.equalsIgnoreCase("uat11avn"));

            if (db_host_19) {
                DB_HOST = Prop.web().getProperty("ORACLE_GENERIC_19C");
            }
            else {
                DB_HOST = Prop.web().getProperty("ORACLE_GENERIC");
            }
        }
        else if (PARAM_ENVIRONMENT.equalsIgnoreCase("live") || PARAM_ENVIRONMENT.equalsIgnoreCase("training")) {
            URL_HUB = Prop.web().getProperty(PARAM_DOMAIN + "_hub_" + PARAM_ENVIRONMENT);
            URL_WEB = Prop.web().getProperty(PARAM_DOMAIN + "_web_" + PARAM_ENVIRONMENT);
            URL_MY = Prop.web().getProperty(PARAM_DOMAIN + "_my_" + PARAM_ENVIRONMENT);
            URL_DOC = Prop.web().getProperty(PARAM_DOMAIN + "_doc_" + PARAM_ENVIRONMENT);

            DB_HOST = Prop.web().getProperty(PARAM_DOMAIN + "_oracle_" + PARAM_ENVIRONMENT);
            DB_UN = Prop.web().getProperty(PARAM_DOMAIN + "_oracle_un_" + PARAM_ENVIRONMENT);
            DB_PW = Prop.web().getProperty(PARAM_DOMAIN + "_oracle_pw_" + PARAM_ENVIRONMENT);
        }
        else {
            URL_HUB = Prop.web().getProperty(PARAM_DOMAIN + "_hub_" + PARAM_ENVIRONMENT);
            URL_WEB = Prop.web().getProperty(PARAM_DOMAIN + "_web_" + PARAM_ENVIRONMENT);
            URL_MY = Prop.web().getProperty(PARAM_DOMAIN + "_my_" + PARAM_ENVIRONMENT);
            URL_DOC = Prop.web().getProperty(PARAM_DOMAIN + "_doc_" + PARAM_ENVIRONMENT);

            DB_HOST = Prop.web().getProperty(PARAM_DOMAIN + "_oracle_" + PARAM_ENVIRONMENT);
            DB_UN = Prop.web().getProperty(PARAM_DOMAIN + "_oracle_un_" + PARAM_ENVIRONMENT);
            DB_PW = Prop.web().getProperty(PARAM_DOMAIN + "_oracle_pw_" + PARAM_ENVIRONMENT);
        }
    }

    public void select_browser(String testng_browser) {
        PARAM_BROWSER = testng_browser.toLowerCase();
        Log.info("[PARAM_BROWSER] " + PARAM_BROWSER);

        firefoxOptions = new FirefoxOptions();
        chromeOptions = new ChromeOptions();
        edgeOptions = new EdgeOptions();
    }

    public void configure_selected_browser() throws MalformedURLException {
        if (PARAM_BROWSER.equalsIgnoreCase("proxy")) {
            chromeOptions.addArguments("--proxy-server=127.0.0.1:8080", "--headless", "--incognito", "--window-size=1920,1080", "--disable-gpu", "--disable-extensions", "--disable-site-isolation-trials", "--no-sandbox","--disable-dev-shm-usage", "--ignore-certificate-errors", "--ignore-ssl-errors=yes");
            driver = new ChromeDriver(chromeOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("chrome")) {
            chromeOptions.addArguments("--headless", "--incognito", "--window-size=1920,1080", "--disable-gpu", "--disable-extensions", "--disable-site-isolation-trials", "--no-sandbox","--disable-dev-shm-usage", "--ignore-certificate-errors");
            driver = new ChromeDriver(chromeOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("chromium")) {
            chromeOptions.setBinary(CHROMIUM_BINARY_ARCH);
            chromeOptions.addArguments("--headless", "--incognito", "--window-size=1920,1080", "--disable-gpu", "--disable-extensions", "--disable-site-isolation-trials", "--no-sandbox","--disable-dev-shm-usage", "--ignore-certificate-errors");
            driver=new ChromeDriver(chromeOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("firefox")) {
            firefoxOptions.addArguments("--headless", "--private", "--width=1920", "--height=1080");
            driver = new FirefoxDriver(firefoxOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("firefox_esr")) {
            firefoxOptions.setBinary(FIREFOX_ESR_BINARY_ARCH);
            firefoxOptions.addArguments("--headless", "--private", "--width=1920", "--height=1080");
            driver = new FirefoxDriver(firefoxOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("gecko")) {
            firefoxOptions.addArguments("--private", "--width=1920", "--height=1080");
            driver = new FirefoxDriver(firefoxOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("gecko_esr")) {
            firefoxOptions.setBinary(FIREFOX_ESR_BINARY_ARCH);
            firefoxOptions.addArguments("--private", "--width=1920", "--height=1080");
            driver = new FirefoxDriver(firefoxOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("edge")) {
            edgeOptions.addArguments("--headless", "--incognito", "--window-size=1920,1080", "--disable-gpu", "--disable-site-isolation-trials");
            driver = new EdgeDriver(edgeOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("msedge")) {
            edgeOptions.addArguments("--incognito", "--window-size=1920,1080", "--disable-gpu", "--disable-site-isolation-trials");
            driver = new EdgeDriver(edgeOptions);
        }
//        else if (PARAM_BROWSER.equalsIgnoreCase("chrome_macos")) {
//            DesiredCapabilities caps = new DesiredCapabilities();
//            caps.setCapability("os", "OS X");
//            caps.setCapability("os_version", "Big Sur");
//            caps.setCapability("browser", "Chrome");
//            caps.setCapability("browser_version", "latest");
//            caps.setCapability("resolution", "1920x1080");
//            caps.setCapability("browserstack.local", "false");
//            caps.setCapability("browserstack.selenium_version", "3.141.59");
//            caps.setCapability("browserstack.local", "false");
//            caps.setCapability("browserstack.local", "false");
//            caps.setCapability("name", BROWSERSTACK_NAME);
//            caps.setCapability("build", BROWSERSTACK_BUILD);
//            driver = new RemoteWebDriver(new URL(URL), caps);
//        }
//        else if (PARAM_BROWSER.equalsIgnoreCase("safari_macos")) {
//            DesiredCapabilities caps = new DesiredCapabilities();
//            caps.setCapability("os", "OS X");
//            caps.setCapability("os_version", "Big Sur");
//            caps.setCapability("browser", "Safari");
//            caps.setCapability("browser_version", "14.1");
//            caps.setCapability("resolution", "1920x1080");
//            caps.setCapability("browserstack.local", "false");
//            caps.setCapability("browserstack.selenium_version", "3.141.59");
//            caps.setCapability("name", BROWSERSTACK_NAME);
//            caps.setCapability("build", BROWSERSTACK_BUILD);
//            driver = new RemoteWebDriver(new URL(URL), caps);
//        }
//        else if (PARAM_BROWSER.equalsIgnoreCase("edge_windows")) {
//            DesiredCapabilities caps = new DesiredCapabilities();
//            caps.setCapability("os", "Windows");
//            caps.setCapability("os_version", "10");
//            caps.setCapability("browser", "Edge");
//            caps.setCapability("browser_version", "latest");
//            caps.setCapability("resolution", "1920x1080");
//            caps.setCapability("browserstack.local", "false");
//            caps.setCapability("browserstack.selenium_version", "3.141.59");
//            caps.setCapability("name", BROWSERSTACK_NAME);
//            caps.setCapability("build", BROWSERSTACK_BUILD);
//            driver = new RemoteWebDriver(new URL(URL), caps);
//        }
//        else if(PARAM_BROWSER.equalsIgnoreCase("tor")) {
//            torProcess = torBrowser.exec(torFirefoxPath + " -n");
//
//            WebDriverManager.firefoxdriver().setup();
//            FirefoxProfile profile = new FirefoxProfile();
//            FirefoxOptions firefoxOptions = new FirefoxOptions();
//
//            profile.setPreference("network.proxy.type", 1);
//            profile.setPreference("network.proxy.socks", "127.0.0.1");
//            profile.setPreference("network.proxy.socks_port", 9150);
//            profile.setPreference("network.proxy.socks_remote_dns", false);
//            firefoxOptions.setProfile(profile);
//            driver = new FirefoxDriver(firefoxOptions);
//        }
        else if(PARAM_BROWSER.equalsIgnoreCase("docker_firefox")) {
            firefoxOptions.addArguments("--private", "--width=1920", "--height=1080");
            String remote_url_firefox = "http://localhost:4444";
            driver = new RemoteWebDriver(new URL(remote_url_firefox), firefoxOptions);
        }
        else if(PARAM_BROWSER.equalsIgnoreCase("docker_chrome")) {
            chromeOptions.addArguments("--incognito", "--window-size=1920,1080", "--disable-gpu", "--disable-extensions", "--disable-site-isolation-trials", "--no-sandbox","--disable-dev-shm-usage", "--ignore-certificate-errors");
            String remote_url_chrome = "http://localhost:4444";
            driver = new RemoteWebDriver(new URL(remote_url_chrome), chromeOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("docker_edge")) {
            edgeOptions.addArguments("--incognito", "--window-size=1920,1080", "--disable-gpu", "--disable-site-isolation-trials");
            String remote_url_edge = "http://localhost:4444";
            driver = new RemoteWebDriver(new URL(remote_url_edge), edgeOptions);
        }
        else if (PARAM_BROWSER.equalsIgnoreCase("safe_mode")) {
            chromeOptions.setBinary(CHROMIUM_BINARY_ARCH);
            chromeOptions.addArguments("--window-size=1920,1080");
            driver = new ChromeDriver(chromeOptions);
        }
        else {
            chromeOptions.setBinary(CHROMIUM_BINARY_ARCH);
            chromeOptions.addArguments("--incognito", "--window-size=1920,1080", "--disable-gpu", "--disable-extensions", "--disable-site-isolation-trials", "--no-sandbox","--disable-dev-shm-usage", "--ignore-certificate-errors");
            driver = new ChromeDriver(chromeOptions);
        }
    }

    public void initiate_webdriver_session() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WD_WAIT));
        js = ((JavascriptExecutor) driver);
    }

    public void take_screenshot(String screenshot) throws IOException {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        FileHandler.copy(source, new File("test-screenshots/ss-" + screenshot + ".png"));
        Log.warn("[SCREENSHOT] SAVED: " + screenshot + ".png");
    }

    public void take_screenshot_full(String screenshot) throws IOException {
        Screenshot s = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
        ImageIO.write(s.getImage(),"PNG",new File("test-screenshots/ss-" + screenshot + ".png"));
        Log.warn("[SCREENSHOT] SAVED: " + screenshot + ".png");
    }

}
