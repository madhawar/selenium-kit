package services;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import systems.Log;
import systems.Prop;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DRIVETRAIN extends Engine {

    protected Connection oracle_connection;
    protected Statement oracle_statement;
    protected ResultSet oracle_result;
    
    private final static String JS_CLICK = "arguments[0].click();";

    public String temp_ab_test_pp() {
        String experimental;
        try {
            experimental = get_value("//input[@name='isNewExperiment']");
        } catch (Exception e) {
            experimental = "false";
        }
        return experimental;
    }

    public void log_qnb_id_web() {
        String id = String.valueOf(js.executeScript("return quote_id"));
        Log.info("QNB ID " + id);
    }

    public void log_qnb_id_hub() {
        driver.switchTo().defaultContent();
        WebElement quoteID = wait_for_element_visible(Prop.elements().getProperty("QUOTE_ID_DISPLAY"));
        String[] id = quoteID.getText().split(": Q");
        driver.switchTo().frame(Prop.elements().getProperty("IFRAME_MO_NAME"));
        Log.info("QNB ID " + id[1]);
    }

    public void execute_js_code(String SCRIPT) {
        js.executeScript(SCRIPT);
        Log.info("[JAVASCRIPT] " + SCRIPT);
    }

    public void sleep() {
        int milliseconds = DEFAULT_THREAD_SLEEP *1000;

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.error("[THREAD SLEEP] Seconds: " + DEFAULT_THREAD_SLEEP);
    }

    public void delta(int CUSTOM_SLEEP) {
        int milliseconds = CUSTOM_SLEEP*1000;

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.error("[THREAD SLEEP] Seconds: " + CUSTOM_SLEEP);
    }

    public void navigate_to(String URL) {
        Log.info("NAVIGATING TO URL: " + URL);
        driver.get(URL);
        Log.info("REDIRECTED TO URL: " + driver.getCurrentUrl());
    }

    public void page_back() {
        driver.navigate().back();
    }

    public void page_forward() {
        driver.navigate().forward();
    }

    public void page_refresh() {
        driver.navigate().refresh();
    }

    public void switch_to_model_office_iframe() {
        driver.switchTo().frame(Prop.elements().getProperty("IFRAME_MO_NAME"));
    }

    public void iframe_switch_to(String LOCATOR) {
        driver.switchTo().frame(LOCATOR);
    }

    public void iframe_default() {
        driver.switchTo().defaultContent();
    }

    public void iframe_default_and_again_switch_to(String LOCATOR) {
        driver.switchTo().defaultContent();
        driver.switchTo().frame(LOCATOR);
    }

    public void open_new_tab_and_switch_immediately() {
        open_new_tab();
        switch_to_tab(1);
    }

    public void open_new_tab() {
        js.executeScript("window.open()");
        Log.info("Opened a new tab (JavaScript)");
    }

    public void switch_to_tab(int id) {
        Log.info("Looking for open tabs...");
        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(id));
        Log.info("Switching to tab by index: " + id);
    }

    public void switch_to_tab1() {
        Log.info("Looking for open tabs...");
        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        Log.info("Switching to tab 1");
    }

    public void clear_cookies() {
        Log.info("CLEARING COOKIES");
        driver.manage().deleteAllCookies();
    }

    public void clear_specific_cookie(String cookie_name) {
        Log.info("CLEARING COOKIE: " + cookie_name);
        driver.manage().deleteCookieNamed(cookie_name);
    }

    public void scroll_to_bottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Log.info("Scrolled to the bottom (JavaScript)");
    }

    public void scroll_to_element(String LOCATOR) {
        WebElement element = wait_for_element_presence(LOCATOR);
        js.executeScript("arguments[0]. scrollIntoView(true);", element);

        Log.info("Scrolled to an element (JavaScript) " + LOCATOR);
    }

    public void wait_for_url_to_have(String URL) {
        wait.until(ExpectedConditions.urlContains(URL));
        Log.info("Waited for URL to have: " + URL);
    }

    public boolean check_for_element_visibility(String LOCATOR) {
        WebElement element;
        boolean visibilityOfElement;
        WebDriverWait wait_alt = new WebDriverWait(driver, Duration.ofSeconds(1));

        if (LOCATOR.startsWith("/")) {
            element = wait_alt.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOCATOR)));
        }
        else if (LOCATOR.startsWith("#")) {
            element= wait_alt.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(LOCATOR)));
        }
        else {
            element= wait_alt.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATOR)));
        }
        Log.info("Waited for element invisible: " + LOCATOR);

        visibilityOfElement = element.isDisplayed();

        return visibilityOfElement;
    }

    public int number_of_elements_presence(String LOCATOR) {
        List<WebElement> elements;
        if (LOCATOR.startsWith("/")) {
            elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(LOCATOR)));
        }
        else if (LOCATOR.startsWith("#")) {
            elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(LOCATOR)));
        }
        else {
            elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(LOCATOR)));
        }

        return elements.size();
    }

    public WebElement wait_for_element_presence(String LOCATOR) {
        WebElement element;

        if (LOCATOR.startsWith("/")) {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(LOCATOR)));
        }
        else if (LOCATOR.startsWith("#")) {
            element= wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(LOCATOR)));
        }
        else {
            element= wait.until(ExpectedConditions.presenceOfElementLocated(By.id(LOCATOR)));
        }
        Log.info("Waited for element presence: " + LOCATOR);

        return element;
    }

    public List<WebElement> wait_for_all_elements_presence(String LOCATOR) {
        List<WebElement> elements;

        if (LOCATOR.startsWith("/")) {
            elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(LOCATOR)));
        }
        else if (LOCATOR.startsWith("#")) {
            elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(LOCATOR)));
        }
        else {
            elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(LOCATOR)));
        }
        Log.info("Waited for element presence: " + LOCATOR);

        return elements;
    }

    public WebElement wait_for_element_visible(String LOCATOR) {
        WebElement element;

        if (LOCATOR.startsWith("/")) {
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOCATOR)));
        }
        else if (LOCATOR.startsWith("#")) {
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(LOCATOR)));
        }
        else {
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATOR)));
        }
        Log.info("Waited for element visibility: " + LOCATOR);

        return element;
    }

    public List<WebElement> wait_for_all_elements_visible(String LOCATOR) {
        List<WebElement> elements;

        if (LOCATOR.startsWith("/")) {
            elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(LOCATOR)));
        }
        else if (LOCATOR.startsWith("#")) {
            elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(LOCATOR)));
        }
        else {
            elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(LOCATOR)));
        }
        Log.info("Waited for element visibility: " + LOCATOR);

        return elements;
    }

    public WebElement wait_for_element_clickable(String LOCATOR) {
        WebElement element;

        if (LOCATOR.startsWith("/")) {
            element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(LOCATOR)));
        }
        else if (LOCATOR.startsWith("#")) {
            element= wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(LOCATOR)));
        }
        else {
            element= wait.until(ExpectedConditions.elementToBeClickable(By.id(LOCATOR)));
        }
        Log.info("Waited for element clickable: " + LOCATOR);

        return element;
    }

    public void wait_for_element_disappear(String LOCATOR) {
        delta(ALTERNATIVE_THREAD_SLEEP);
        if (LOCATOR.startsWith("/")) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(LOCATOR)));
        }
        else if (LOCATOR.startsWith("#")) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(LOCATOR)));
        }
        else {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(LOCATOR)));
        }
        Log.info("Waited for element invisibility: " + LOCATOR);
    }

    public boolean wait_for_element_else_skip(String LOCATOR) {
        boolean WebElement = false;
        List<WebElement> elements;
        WebDriverWait wait_alt = new WebDriverWait(driver, Duration.ofSeconds(ALTERNATIVE_WD_WAIT));

        try {
            if (LOCATOR.startsWith("/")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(LOCATOR)));
            }
            else if (LOCATOR.startsWith("#")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(LOCATOR)));
            }
            else {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(LOCATOR)));
            }

            if(elements.isEmpty()) {
                Log.error("No such element visible.");
                WebElement = false;
            }
            else {
                Log.error("Found element visible.");
                WebElement = true;
            }
        }
        catch (TimeoutException e) {
            Log.error("Skipping action as element not present within expected time in seconds: " + ALTERNATIVE_WD_WAIT);
            WebElement = false;
        }

        return WebElement;
    }

    public void iframe_switch_if_present(String LOCATOR) {
        List<WebElement> elements;
        WebDriverWait wait_alt = new WebDriverWait(driver, Duration.ofSeconds(ALTERNATIVE_WD_WAIT));

        try {
            if (LOCATOR.startsWith("/")) {
                elements = wait_alt.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(LOCATOR)));
            }
            else if (LOCATOR.startsWith("#")) {
                elements = wait_alt.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(LOCATOR)));
            }
            else {
                elements = wait_alt.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(LOCATOR)));
            }

            if(elements.isEmpty()) {
                Log.warn("Skipping element click as it is not visible.");
            }
            else {
                for(WebElement element : elements) {
                    String klass = element.getClass().toString();
                    driver.switchTo().frame(LOCATOR);
                    Log.info("Switching to frame " + klass);
                }
            }
        }
        catch (TimeoutException e) {
            Log.error("Skipping action as element not present within expected time in seconds: " + ALTERNATIVE_WD_WAIT);
        }
    }

    public void element_click_if_visible_else_skip(String LOCATOR) {
        Log.info("Waiting for an element...");
        List<WebElement> elements;
        WebDriverWait wait_alt = new WebDriverWait(driver, Duration.ofSeconds(ALTERNATIVE_WD_WAIT));

        try {
            if (LOCATOR.startsWith("/")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(LOCATOR)));
            }
            else if (LOCATOR.startsWith("#")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(LOCATOR)));
            }
            else {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(LOCATOR)));
            }

            if(elements.isEmpty()) {
                Log.warn("Skipping element click as it is not visible.");
            }
            else {
                for(WebElement element : elements) {
                    element.click();
                    Log.info("Clicked element: " + LOCATOR);
                }
            }
        }
        catch (TimeoutException e) {
            Log.error("Skipping action as element not present within expected time in seconds: " + ALTERNATIVE_WD_WAIT);
        }
    }

    public void element_click_js_if_visible_else_skip(String LOCATOR) {
        List<WebElement> elements;
        WebDriverWait wait_alt = new WebDriverWait(driver, Duration.ofSeconds(ALTERNATIVE_WD_WAIT));

        try {
            if (LOCATOR.startsWith("/")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(LOCATOR)));
            }
            else if (LOCATOR.startsWith("#")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(LOCATOR)));
            }
            else {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(LOCATOR)));
            }

            if(elements.isEmpty()) {
                Log.warn("Skipping element click as it is not visible.");
            }
            else {
                for(WebElement element : elements) {
                    js.executeScript(JS_CLICK, element);
                    Log.info("Clicked element (JavaScript): " + LOCATOR);
                }
            }
        }
        catch (TimeoutException e) {
            Log.error("Skipping action as element not present within expected time in seconds: " + ALTERNATIVE_WD_WAIT);
        }
    }

    public void input_text_if_visible_else_skip(String LOCATOR, String TEXT) {
        List<WebElement> elements;
        WebDriverWait wait_alt = new WebDriverWait(driver, Duration.ofSeconds(ALTERNATIVE_WD_WAIT));

        try {
            if (LOCATOR.startsWith("/")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(LOCATOR)));
            }
            else if (LOCATOR.startsWith("#")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(LOCATOR)));
            }
            else {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(LOCATOR)));
            }

            if(elements.isEmpty()) {
                Log.warn("Skipping element click as it is not visible.");
            }
            else {
                for(WebElement element : elements) {
                    element.sendKeys(TEXT);
                    Log.info("Entered text: " + TEXT + " for: " + LOCATOR);
                }
            }
        }
        catch (TimeoutException e) {
            Log.error("Skipping action as element not present within expected time in seconds: " + ALTERNATIVE_WD_WAIT);
        }
    }

    public void check_checkbox_if_not_checked(String LOCATOR) {
        WebElement element = wait_for_element_presence(LOCATOR);
        if (!element.isSelected()) {
            element.click();
        }
    }

    public void check_checkbox(String LOCATOR) {
        WebElement element = wait_for_element_presence(LOCATOR);
        element.click();
        Log.info("CHECKED CHECKBOX: " + LOCATOR);
    }

    public boolean check_checkbox_checked_unchecked_state(String LOCATOR) {
        WebElement element = wait_for_element_presence(LOCATOR);
        Log.info("[ASSERTION] Verifying Checkbox Checked/ Unchecked state of " + LOCATOR);
        return element.isSelected();
    }

    public boolean check_element_enabled_disabled_state(String LOCATOR) {
        WebElement element = wait_for_element_presence(LOCATOR);
        return element.isEnabled();
    }

    public String get_text(String LOCATOR) {
        String inner_text;
        WebElement element = wait_for_element_presence(LOCATOR);
        inner_text = element.getText().trim().replaceAll("[\\r\\n]", "");
        return inner_text;
    }

    public String get_inner_text(String LOCATOR) {
        String inner_text;
        WebElement element = wait_for_element_presence(LOCATOR);
        inner_text = element.getAttribute("innerHTML").trim().replaceAll("[\\r\\n]", "");
        return inner_text;
    }

    public String get_value(String LOCATOR) {
        String value;
        WebElement element = wait_for_element_presence(LOCATOR);
        value = element.getAttribute("value").trim().replaceAll("[\\r\\n]", "");
        return value;
    }

    public String get_id(String LOCATOR) {
        String id;
        WebElement element = wait_for_element_presence(LOCATOR);
        id = element.getAttribute("id").trim().replaceAll("[\\r\\n]", "");
        return id;
    }

    public void press_tab(String LOCATOR) {
        WebElement input = wait_for_element_presence(LOCATOR);
        input.sendKeys(Keys.TAB);
        Log.info("Pressed TAB key");
    }

    public void press_enter(String LOCATOR) {
        WebElement input = wait_for_element_presence(LOCATOR);
        input.sendKeys(Keys.ENTER);
        Log.info("Pressed ENTER key");
    }

    public void page_down(String LOCATOR) {
        WebElement input = wait_for_element_presence(LOCATOR);
        input.sendKeys(Keys.PAGE_DOWN);
        Log.info("Pressed ENTER key");
    }

    public void page_up(String LOCATOR) {
        WebElement input = wait_for_element_presence(LOCATOR);
        input.sendKeys(Keys.PAGE_UP);
        Log.info("Pressed ENTER key");
    }

    public void press_esc(String LOCATOR) {
        WebElement input = wait_for_element_presence(LOCATOR);
        input.sendKeys(Keys.ESCAPE);
        Log.info("Pressed ESCAPE key");
    }

    public void input_letter_by_letter(String LOCATOR, String TEXT) {
        WebElement input = wait_for_element_presence(LOCATOR);
        char[] letterX = TEXT.toCharArray();
        for (char s : letterX) {
            input.sendKeys(String.valueOf(s));
        }
        Log.info("Entered text: " + TEXT + " for: " + LOCATOR);
    }

    public void input_clear(String LOCATOR) {
        WebElement input = wait_for_element_presence(LOCATOR);
        input.clear();
        Log.info("Cleared text of: " + LOCATOR);
    }

    public void input_text(String LOCATOR, String TEXT) {
        WebElement input = wait_for_element_presence(LOCATOR);
        input.clear();
        input.sendKeys(TEXT);
        Log.info("Entered text: " + TEXT + " for: " + LOCATOR);
    }

    public void input_text_and_press_enter(String LOCATOR, String TEXT) {
        WebElement input = wait_for_element_presence(LOCATOR);
        input.clear();
        input.sendKeys(TEXT);
        Log.info("Entered text: " + TEXT + " for: " + LOCATOR);
        press_enter(LOCATOR);
    }

    public void input_text_and_press_tab(String LOCATOR, String TEXT) {
        WebElement input = wait_for_element_presence(LOCATOR);
        input.clear();
        input.sendKeys(TEXT);
        Log.info("Entered text: " + TEXT + " for: " + LOCATOR);
        press_tab(LOCATOR);
    }

    public void input_text_when_displayed(String LOCATOR, String TEXT) {
        WebElement input = wait_for_element_visible(LOCATOR);
        input.clear();
        input.sendKeys(TEXT);
        Log.info("Entered text: " + TEXT + " for: " + LOCATOR);
    }

    public void input_text_once_clickable(String LOCATOR, String TEXT) {
        WebElement input = wait_for_element_clickable(LOCATOR);
        input.clear();
        input.sendKeys(TEXT);
        Log.info("Entered text: " + TEXT + " for: " + LOCATOR);
    }

    public void input_textarea_js_inner_text(String LOCATOR, String TEXT) {
        WebElement input = wait_for_element_presence(LOCATOR);
//        WebElement input = driver.findElement(By.xpath(LOCATOR));
        js.executeScript("arguments[0].innerText = '" + TEXT + "'", input);
        input.sendKeys(Keys.ENTER);
        Log.info("Entered text (JavaScript): " + LOCATOR);
    }

    public void input_textarea_js_value(String LOCATOR, String TEXT) {
        WebElement input = wait_for_element_presence(LOCATOR);
//        WebElement input = driver.findElement(By.xpath(LOCATOR));
        js.executeScript("document.getElementById('" + LOCATOR + "').value='" + TEXT + "';", input);
        input.sendKeys(Keys.ENTER);
        Log.info("Entered text (JavaScript): " + LOCATOR);
    }

    public void select_dropdown_list_by_visible_text_when_clickable(String LOCATOR, String VISIBLE_TEXT) {
        WebElement select = wait_for_element_clickable(LOCATOR);
        Select value = new Select(select);
        value.selectByVisibleText(VISIBLE_TEXT);
        Log.info("Selected: " + VISIBLE_TEXT + " for: " + LOCATOR);
    }

    public void select_dropdown_list_by_visible_text(String LOCATOR, String VISIBLE_TEXT) {
        WebElement select = wait_for_element_presence(LOCATOR);
        Select value = new Select(select);
        value.selectByVisibleText(VISIBLE_TEXT);
        Log.info("Selected: " + VISIBLE_TEXT + " for: " + LOCATOR);
    }

    public void select_dropdown_list_by_value(String LOCATOR, String VALUE) {
        WebElement select = wait_for_element_presence(LOCATOR);
        Select value = new Select(select);
        value.selectByValue(VALUE);
        Log.info("Selected: " + VALUE + " for: " + LOCATOR);
    }

    public void select_dropdown_list_by_index(String LOCATOR, int INDEX) {
        WebElement select = wait_for_element_presence(LOCATOR);
        Select value = new Select(select);
        value.selectByIndex(INDEX);
        Log.info("Selected: " + INDEX + " for: " + LOCATOR);
    }

    public int select_dropdown_fetch_selected_index(String LOCATOR) {
        WebElement select = wait_for_element_presence(LOCATOR);
        Select value = new Select(select);
        return value.getOptions().indexOf(value.getFirstSelectedOption());
    }

    public void select_dropdown_list_last_item(String LOCATOR) {
        WebElement select = wait_for_element_presence(LOCATOR);
        Select value = new Select(select);
        value.selectByIndex(value.getOptions().size()-1);
        Log.info("Selected last item of: " + LOCATOR);
    }

    public void element_click(String LOCATOR) {
        WebElement element = wait_for_element_presence(LOCATOR);
        element.click();
        Log.info("Clicked element: " + LOCATOR);
    }

    public void element_click_js(String LOCATOR) {
        WebElement element = wait_for_element_presence(LOCATOR);
        js.executeScript(JS_CLICK, element);
        Log.info("Clicked element (JavaScript): " + LOCATOR);
    }

    public void element_click_when_displayed(String LOCATOR) {
        WebElement element = wait_for_element_visible(LOCATOR);
        element.click();
        Log.info("Clicked element: " + LOCATOR);
    }

    public void element_click_js_when_displayed(String LOCATOR) {
        WebElement element = wait_for_element_visible(LOCATOR);
        js.executeScript(JS_CLICK, element);
        Log.info("Clicked element (JavaScript): " + LOCATOR);
    }

    public void element_click_once_clickable(String LOCATOR) {
        // https://stackoverflow.com/a/56361554
        WebElement element = wait_for_element_clickable(LOCATOR);
        element.click();
        Log.info("Clicked element: " + LOCATOR);
    }

    public void element_click_js_once_clickable(String LOCATOR) {
        // https://stackoverflow.com/a/56361554
        WebElement element = wait_for_element_clickable(LOCATOR);
        js.executeScript(JS_CLICK, element);
        Log.info("Clicked element (JavaScript): " + LOCATOR);
    }

    public void form_submit(String LOCATOR) {
        WebElement form = wait_for_element_presence(LOCATOR);
        form.submit();
        Log.info("Submitted form: " + LOCATOR);
    }

    public void remove_read_only_status_js(String ID) {
        js.executeScript ("document.getElementById('" + ID + "').removeAttribute('readonly',0);");
        Log.info("Removed read-only status of an element (JavaScript)");
    }

    public void assert_input_feedback_valid(String INPUT, String ACTION, String ERROR, String VALID_INPUT) {
        if (ACTION.isEmpty()) {
            input_text_and_press_tab(INPUT, VALID_INPUT);
            delta(1);
            assert_element_not_visible(ERROR);
        }
        else {
            input_text(INPUT, VALID_INPUT);
            element_click(ACTION);
            delta(1);
            assert_element_not_visible(ERROR);
        }
    }

    public void assert_input_feedback_invalid(String INPUT, String ACTION, String ERROR, String INVALID_INPUT, String EXPECTED_ERROR) {
        if (ACTION.isEmpty()) {
            input_text_and_press_tab(INPUT, INVALID_INPUT);
            delta(1);
            assert_inner_text(ERROR, EXPECTED_ERROR);
        }
        else {
            input_text(INPUT, INVALID_INPUT);
            element_click(ACTION);
            delta(1);
            assert_inner_text(ERROR, EXPECTED_ERROR);
        }
    }

    public void assert_input_feedback_invalid_soft(String INPUT, String ACTION, String ERROR, String INVALID_INPUT, String EXPECTED_ERROR) {
        if (ACTION.isEmpty()) {
            input_text_and_press_tab(INPUT, INVALID_INPUT);
            delta(1);
            assert_inner_text_soft(ERROR, EXPECTED_ERROR);
        }
        else {
            input_text(INPUT, INVALID_INPUT);
            element_click(ACTION);
            delta(1);
            assert_inner_text_soft(ERROR, EXPECTED_ERROR);
        }
    }

    public void select_all_items_in_dropdown_one_at_a_time(String LOCATOR, String VALUES_LIST) {
        String[] VALUES = VALUES_LIST.split("\\|");

        for (String SELECTION : VALUES) {
            select_dropdown_list_by_visible_text(LOCATOR, SELECTION);
        }
    }

    public void assert_dropdown_content(String EXPECTED_RESULT, String LOCATOR) {
        String[] EXPECTED_RESULTS_LIST = EXPECTED_RESULT.split("\\|");

        Select dropDownSelect = new Select(wait_for_element_visible(LOCATOR));
        List<WebElement> allOptions = dropDownSelect.getOptions();
        for (int i=0;i<allOptions.size();i++) {
            Assert.assertEquals(allOptions.get(i).getText().trim().toLowerCase(), EXPECTED_RESULTS_LIST[i].trim().toLowerCase());
            Log.info("FOUND ITEMS: " + allOptions.get(i).getText());
        }
    }

    public void assert_dropdown_selected_value(String EXPECTED_RESULT, String LOCATOR) {
        String[] EXPECTED_RESULTS_LIST = EXPECTED_RESULT.split("\\|");

        Select dropDownSelect = new Select(wait_for_element_presence(LOCATOR));
        String selectedOption = dropDownSelect.getFirstSelectedOption().getText();
        Assert.assertEquals(selectedOption, EXPECTED_RESULTS_LIST[0]);
        Log.info("SELECTED VALUE: " + selectedOption);
    }

    public void assert_button_text(String LOCATOR, String VALUE) {
        WebElement button = wait_for_element_presence(LOCATOR);
        Assert.assertEquals(button.getAttribute("value"), VALUE);
        Log.info("[ASSERTION] Asserted button text: " + VALUE + " for: " + LOCATOR);
    }

    public void assert_url(String CONTENT) {
        Assert.assertEquals(driver.getCurrentUrl(), CONTENT, "[FAILED] Unable to match expected URL with: " + driver.getCurrentUrl());
        Log.info("[ASSERTION] Verified: " + CONTENT);
    }

    public void assert_url_soft(String CONTENT) {
        boolean findInURL = wait.until(ExpectedConditions.urlContains(CONTENT));
        Assert.assertTrue(findInURL, "[FAILED] Unable to match expected URL with: " + driver.getCurrentUrl());
        Log.info("[ASSERTION] Verified: " + CONTENT);
    }

    public void assert_value(String LOCATOR, String MESSAGE) {
        String text;

        if (LOCATOR.startsWith("/")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOCATOR))).getAttribute("value").trim().replaceAll("[\\r\\n]", "");
            Log.info("Locating XPATH: " + LOCATOR);
        }
        else if (LOCATOR.startsWith("#")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(LOCATOR))).getAttribute("value").trim().replaceAll("[\\r\\n]", "");
            Log.info("Locating CSS: " + LOCATOR);
        }
        else {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATOR))).getAttribute("value").trim().replaceAll("[\\r\\n]", "");
            Log.info("Locating ID: " + LOCATOR);
        }

        Assert.assertEquals(text, MESSAGE, "[FAILED] Text verification");
        Log.info("[ASSERTION] Verified message: " + text);
    }

    public void assert_text(String LOCATOR, String MESSAGE) {
        String text;

        if (LOCATOR.startsWith("/")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOCATOR))).getText().trim().replaceAll("[\\r\\n]", " ");
            Log.info("Locating XPATH: " + LOCATOR);
        }
        else if (LOCATOR.startsWith("#")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(LOCATOR))).getText().trim().replaceAll("[\\r\\n]", " ");
            Log.info("Locating CSS: " + LOCATOR);
        }
        else {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATOR))).getText().trim().replaceAll("[\\r\\n]", " ");
            Log.info("Locating ID: " + LOCATOR);
        }

        Assert.assertEquals(text, MESSAGE, "[FAILED] Text verification");
        Log.info("[ASSERTION] Verified message: " + text);
    }

    public void assert_text_soft(String LOCATOR, String MESSAGE) {
        String text;

        if (LOCATOR.startsWith("/")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOCATOR))).getText().trim().replaceAll("[\\r\\n]", " ");
        }
        else if (LOCATOR.startsWith("#")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(LOCATOR))).getText().trim().replaceAll("[\\r\\n]", " ");
        }
        else {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATOR))).getText().trim().replaceAll("[\\r\\n]", " ");
        }

        boolean findText = text.contains(MESSAGE);
        Assert.assertTrue(findText, "[FAILED] Text verification: " + MESSAGE);
        Log.info("[ASSERTION] Verified message: " + text);
    }

    public void assert_inner_text(String LOCATOR, String MESSAGE) {
        String text;

        if (LOCATOR.startsWith("/")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOCATOR))).getAttribute("innerHTML").trim().replaceAll("[\\r\\n]", " ");
            Log.info("Locating XPATH: " + LOCATOR);
        }
        else if (LOCATOR.startsWith("#")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(LOCATOR))).getAttribute("innerHTML").trim().replaceAll("[\\r\\n]", " ");
            Log.info("Locating CSS: " + LOCATOR);
        }
        else {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATOR))).getAttribute("innerHTML").trim().replaceAll("[\\r\\n]", " ");
            Log.info("Locating ID: " + LOCATOR);
        }

        Assert.assertEquals(text, MESSAGE, "[FAILED] Inner text verification");
        Log.info("[ASSERTION] Verified message: " + text);
    }

    public void assert_inner_text_soft(String LOCATOR, String MESSAGE) {
        String text;

        if (LOCATOR.startsWith("/")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOCATOR))).getAttribute("innerHTML").trim().replaceAll("[\\r\\n]", " ");
            Log.info("Locating XPATH: " + LOCATOR);
        }
        else if (LOCATOR.startsWith("#")) {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(LOCATOR))).getAttribute("innerHTML").trim().replaceAll("[\\r\\n]", " ");
            Log.info("Locating CSS: " + LOCATOR);
        }
        else {
            text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATOR))).getAttribute("innerHTML").trim().replaceAll("[\\r\\n]", " ");
            Log.info("Locating ID: " + LOCATOR);
        }

        boolean findText = text.contains(MESSAGE);
        Assert.assertTrue(findText, "[FAILED] Text verification: " + MESSAGE);
    }

    public void assert_element_not_visible(String LOCATOR) {
        List<WebElement> elements;
        WebDriverWait wait_alt = new WebDriverWait(driver, Duration.ofSeconds(ALTERNATIVE_WD_WAIT));

        try {
            if (LOCATOR.startsWith("/")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(LOCATOR)));
            }
            else if (LOCATOR.startsWith("#")) {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(LOCATOR)));
            }
            else {
                elements = wait_alt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(LOCATOR)));
            }

            if (elements.isEmpty()) {
                Log.warn("[ASSERTION] PASS.");
            }
            else {
                Log.warn("[ASSERTION] FAIL. Element is visible.");
                Assert.fail();
            }
        }
        catch (TimeoutException e) {
            Log.error("[ASSERTION] PASS. Element not visible as expected.");
        }
    }

    public void assert_element_is_disabled(String LOCATOR) {
        boolean elementState = check_element_enabled_disabled_state(LOCATOR);
        Assert.assertFalse(elementState);

        Log.info("[ASSERTION] Verifying element Disable state of " + LOCATOR);
    }

    public void assert_element_is_enabled(String LOCATOR) {
        boolean elementState = check_element_enabled_disabled_state(LOCATOR);
        Assert.assertTrue(elementState);

         Log.info("[ASSERTION] Verifying element Enable state of " + LOCATOR);
    }

    public void assert_element_is_visible(String LOCATOR) {
        WebElement element = wait_for_element_visible(LOCATOR);
        boolean elementVisible = element.isDisplayed();
        Assert.assertTrue(elementVisible);
        Log.info("[ASSERTION] Element visible: " + LOCATOR);
    }

    public void assert_element_value(String LOCATOR, String EXPECTED_VALUE) {
        WebElement element = wait_for_element_visible(LOCATOR);
        String ACTUAL_VALUE = element.getAttribute("value");
        Assert.assertEquals(ACTUAL_VALUE, EXPECTED_VALUE);
        Log.info("[ASSERTION] Element value found: " + ACTUAL_VALUE);
    }

    public void assert_checkbox_checked(String LOCATOR) {
        WebElement element = wait_for_element_presence(LOCATOR);
        boolean check_status = element.isSelected();

        Assert.assertTrue(check_status);
    }

    public void assert_checkbox_not_checked(String LOCATOR) {
        WebElement element = wait_for_element_presence(LOCATOR);
        boolean check_status = element.isSelected();

        Assert.assertFalse(check_status);
    }

    public void getMyAccountRegistrationURL(String SUBJECT, String SQL_QUERY, String email, String password, String ORACLE_HOST, String ORACLE_USERNAME, String ORACLE_PASSWORD, String ENVIRONMENT, String DOMAIN) throws SQLException {
        String MYACC_VERIFICATION_EMAIL_CONTENT = null;
        String MYACC_VERIFICATION_URL_ACTIVATION_KEY = null;
        String URL_IN_THE_EMAIL = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            oracle_connection = DriverManager.getConnection(ORACLE_HOST, ORACLE_USERNAME, ORACLE_PASSWORD);
            if (oracle_connection != null) {
                Log.info("[DB] CONNECTION SUCCESS!");
                oracle_statement=oracle_connection.createStatement();
                oracle_result=oracle_statement.executeQuery(SQL_QUERY);
                Log.info("[DB] EXECUTING: " + SQL_QUERY);
                while(oracle_result.next()) {
                    MYACC_VERIFICATION_EMAIL_CONTENT = oracle_result.getString(1);
                    Pattern pattern = Pattern.compile("key=(.+?)\"", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(Objects.requireNonNull(MYACC_VERIFICATION_EMAIL_CONTENT));
                    while (matcher.find()) {
                        MYACC_VERIFICATION_URL_ACTIVATION_KEY = matcher.group();
                    }
                }
                Log.info("[DB] EXECUTED: " + SQL_QUERY);
            }
            if (SUBJECT.equalsIgnoreCase("ACTIVATE")) {
                URL_IN_THE_EMAIL = ENVIRONMENT + Prop.web().getProperty("ACTIVATE_ACCOUNT_CREATION_URL") + MYACC_VERIFICATION_URL_ACTIVATION_KEY.substring(0, MYACC_VERIFICATION_URL_ACTIVATION_KEY.length() - 1);
            }
            else if (SUBJECT.equalsIgnoreCase("RESET")) {
                URL_IN_THE_EMAIL = ENVIRONMENT + Prop.web().getProperty("PASSWORD_RESET_URL") + MYACC_VERIFICATION_URL_ACTIVATION_KEY.substring(0, MYACC_VERIFICATION_URL_ACTIVATION_KEY.length() - 1);
            }
            else if (SUBJECT.equalsIgnoreCase("CHANGE")) {
                URL_IN_THE_EMAIL = ENVIRONMENT + Prop.web().getProperty("ACTIVATE_USERNAME_CHANGE_URL") + MYACC_VERIFICATION_URL_ACTIVATION_KEY.substring(0, MYACC_VERIFICATION_URL_ACTIVATION_KEY.length() - 1);
            }
            else {
                Log.info("UNKNOWN EMAIL SUBJECT.");
            }
            Log.info("Imitate clicking URL: " + URL_IN_THE_EMAIL);
            driver.get(URL_IN_THE_EMAIL);
            Log.info("Activated email: " + email);
        }
        catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (oracle_connection != null && !oracle_connection.isClosed()) {
                    oracle_connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void createAccount(String URL, String DOMAIN, String ORACLE_HOSTNAME, String ORACLE_USERNAME, String ORACLE_PASSWORD, String EMAIL, String PASSWORD) throws SQLException {
        navigate_to(URL);
        element_click(Prop.elements().getProperty("MY_BUTTON_CREATE_AN_ACCOUNT"));

        input_text(Prop.elements().getProperty("MY_INPUT_EMAIL"), EMAIL);
        input_text(Prop.elements().getProperty("MY_INPUT_PASSWORD"), PASSWORD);
        element_click(Prop.elements().getProperty("MY_BUTTON_REGISTER"));

        String SQL_ACTIVATE_ACCOUNT = Prop.web().getProperty("ACTIVATE_ACCOUNT_CREATION_SQL") + EMAIL + Prop.web().getProperty("SQL_END_MESSAGE_ID");
        getMyAccountRegistrationURL("ACTIVATE", SQL_ACTIVATE_ACCOUNT, EMAIL, PASSWORD, ORACLE_HOSTNAME, ORACLE_USERNAME, ORACLE_PASSWORD, URL, DOMAIN);

        select_dropdown_list_by_visible_text(Prop.elements().getProperty("MY_DETAILS_TITLE"), Prop.generic().getProperty("TITLE"));
        input_text(Prop.elements().getProperty("MY_DETAILS_FIRST_NAME"), Prop.generic().getProperty("FIRST_NAME"));
        input_text(Prop.elements().getProperty("MY_DETAILS_LAST_NAME"), Prop.generic().getProperty("LAST_NAME"));
        input_text(Prop.elements().getProperty("MY_DETAILS_TELEPHONE"), Prop.generic().getProperty("PHONE"));
        input_text(Prop.elements().getProperty("MY_DETAILS_DOB_DD"), Prop.generic().getProperty("DOB_DD"));
        input_text(Prop.elements().getProperty("MY_DETAILS_DOB_MM"), Prop.generic().getProperty("DOB_MM"));
        input_text(Prop.elements().getProperty("MY_DETAILS_DOB_YYYY"), Prop.generic().getProperty("DOB_YYYY"));
        input_text(Prop.elements().getProperty("MY_DETAILS_POSTCODE"), Prop.generic().getProperty("POSTCODE"));
        element_click(Prop.elements().getProperty("MY_DETAILS_BUTTON_FIND_ADDRESS"));
        select_dropdown_list_by_visible_text(Prop.elements().getProperty("MY_DETAILS_ADDRESS_LIST"), Prop.generic().getProperty("POSTCODE_ADDRESS_LIST"));
        element_click(Prop.elements().getProperty("MY_DETAILS_BUTTON_SAVE"));
    }

    public String fetchLoginEmail(String DOMAIN) {
        if (DOMAIN.equalsIgnoreCase("STAYSURE")) {
            log_in_email = Prop.tmp().getProperty("sts_my_acc_email");
        }
        else if (DOMAIN.equalsIgnoreCase("AVANTI")) {
            log_in_email = Prop.tmp().getProperty("avn_my_acc_email");
        }
        else {
            Log.info("DOMAIN NOT SPECIFIED!");
        }

        return log_in_email;
    }

    public String fetchLoginPassword(String DOMAIN) {
        if (DOMAIN.equalsIgnoreCase("STAYSURE")) {
            log_in_password = Prop.tmp().getProperty("sts_my_acc_password");
        }
        else if (DOMAIN.equalsIgnoreCase("AVANTI")) {
            log_in_password = Prop.tmp().getProperty("avn_my_acc_password");
        }
        else {
            Log.info("DOMAIN NOT SPECIFIED!");
        }

        return log_in_password;
    }

    public void captureFullPageScreenshotFirefox(String screenshot) throws IOException {
        File src = ((FirefoxDriver)driver).getFullPageScreenshotAs(OutputType.FILE);
        FileHandler.copy(src, new File("test-screenshots/" + screenshot + ".png"));
        Log.warn("[SCREENSHOT] SAVED: " + screenshot + ".png");
    }

}
