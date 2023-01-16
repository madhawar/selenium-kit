package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import services.DRIVETRAIN;
import systems.Log;
import systems.Prop;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SampathBankEX extends DRIVETRAIN {

    protected DateTimeFormatter ss = DateTimeFormatter.ofPattern("uuuu-MM-dd-HH-mm-ss");

    protected String SAMPATH_BANK_WEB = "https://www.sampath.lk/en/exchange-rates";
    protected String LOCATOR_DATE_TIME = "//*[@id='rates']/child::div/child::div/child::p";
    protected String LOCATOR_GBP_TT_BUYING_GBP ="//*[@id='rates']/child::ul/following-sibling::div/child::div/child::table/child::tbody/child::tr[16]/child::td[1]";
    protected String LOCATOR_GBP_TT_BUYING = "//*[@id='rates']/child::ul/following-sibling::div/child::div/child::table/child::tbody/child::tr[16]/child::td[2]";

    public SampathBankEX(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WD_WAIT));
        js = ((JavascriptExecutor) driver);
    }

    public void sampath_bank_uk_gbp_tt_buying_rate() throws IOException {
        LocalDateTime now = LocalDateTime.now();

        navigate_to(SAMPATH_BANK_WEB);
        assert_url(SAMPATH_BANK_WEB);
        element_click_js_if_visible_else_skip("adroll_allow_all");
        captureFullPageScreenshotFirefox("uk-pound-tt-buying-" + ss.format(now));

        String TODAY_DATE_TIME = get_text(LOCATOR_DATE_TIME);
        String TODAY_GBP_TT_BUYING_GBP = get_text(LOCATOR_GBP_TT_BUYING_GBP);
        String TODAY_GBP_TT_BUYING = get_text(LOCATOR_GBP_TT_BUYING);
        Log.info("[SAMPATH_BANK] " + TODAY_DATE_TIME + ": " +  TODAY_GBP_TT_BUYING + " " + TODAY_GBP_TT_BUYING_GBP);

        Prop.sampathExRate("TIMESTAMP", TODAY_DATE_TIME);
        Prop.sampathExRate("CURRENCY", TODAY_GBP_TT_BUYING_GBP);
        Prop.sampathExRate("VALUE", TODAY_GBP_TT_BUYING);
    }
}
