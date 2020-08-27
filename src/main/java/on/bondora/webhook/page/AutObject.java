package on.bondora.webhook.page;

import com.google.common.base.Optional;

import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.touch.FlickAction;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Test framework wrappers of native RemoteWebDriver API.
 * <p>
 * AutObject is an abbreviation of "Application Under Test Object".
 * An AutObject is a locator of an object from the Application Under Test (AUT),
 * e.g. a web element from custcom, such as a button.
 *
 * @author SET (Automation team)
 */

public class AutObject extends RemoteWebElement {

    protected RemoteWebDriver parent;
    public By by;
    protected String name;
    private Map<WebElement, WebElement> frameElements;
    private Optional<WebElement> webElement = Optional.absent();
    private boolean elementAlreadyExists = false;
    private By parentIFrame;

    protected WebElement parentFrame;


    private enum SearchMethod {
        BY_VALUE,
        BY_TEXT
    }

    /**
     * Get remote WebDriver.
     *
     * @return WebDriver for native API calls
     */
    public RemoteWebDriver getParent() {
        return parent;
    }

    /**
     * Get parent frame of the AutObject.
     *
     * @return parent frame
     */
    public WebElement getParentFrame() {
        return parentFrame;
    }

    /**
     * Default constructor for AutObject.
     */
    public AutObject() {
        initialise();
    }

    /**
     * Constructor for AutObject.
     *
     * @param name The name of the AutObject
     * @param by   The value of the "id" attribute to search for
     * @see #AutObject(String, By, By)
     */
    public AutObject(String name, By by) {
        this(name, by, null);
    }

    /**
     * Constructor for AutObject based on iFrame. Specify the iFrame in which to search for the WebElement.
     *
     * @param name           The name of the AutObject
     * @param by             The value of the "id" attribute to search for
     * @param byParentIFrame The value of the iFrame attribute to search for
     */
    public AutObject(String name, By by, By byParentIFrame) {
        initialise();
        parentFrame = null;
        this.parentIFrame = byParentIFrame;
        this.name = name;
        this.by = by;
        this.setFileDetector(parent.getFileDetector());
    }

    /**
     * Constructor for AutObject based on a WebElement.
     *
     * @param name    The name of the AutObject
     * @param element The Selenium WebElement on which to construct the AutObject
     * @see #AutObject(String, WebElement, By)
     * @deprecated use {@link #AutObject(String, AutObject, By)} instead
     */
    @Deprecated
    public AutObject(String name, WebElement element) {
        this(name, element, null);
    }

    /**
     * Constructor for AutObject based on a WebElement and by (The by is needed when attempting to refind the element after a stale element exception).
     *
     * @param name    The name of the AutObject
     * @param element The Selenium WebElement on which to construct the AutObject
     * @param by      The locator initially used
     * @see #AutObject(String, By)
     * @deprecated use {@link #AutObject(String, AutObject, By)} instead
     */
    @Deprecated
    public AutObject(String name, WebElement element, By by) {
        this(name, by);
        this.webElement = Optional.of(element);
        setId(((RemoteWebElement) element).getId());
        this.elementAlreadyExists = true;
    }

    /**
     * Creates an AutObject whose By locator is based on two <i>chained</i> locators:
     * the locator of the root and the other given locator.
     * <p>
     * This should be preferred over the constructors taking a WebElement,
     * i.e. {@link #AutObject(String, WebElement)} and {@link #AutObject(String, WebElement, By)},
     * as those create instances that are not very consistent.
     *
     * @param name
     * @param root             the root element, from which to start the search in the DOM
     * @param locatorUnderRoot the locator to be "attached" to the root in order to find elements
     * @see ByChained
     */
    public AutObject(String name, AutObject root, By locatorUnderRoot) {
        this(name, new ByChained(root.by, locatorUnderRoot));
    }

    private void initialise() {
        this.parent = DriverFactory.getDriverFactory().getRemoteWebDriver();
        setParent(this.parent);
        frameElements = new HashMap<>();
    }

    @SneakyThrows
    public <T> void waitUntil(ExpectedCondition<T> expectedCondition, long timeoutInSecs) {
        try {
            WebDriverWait wait = new WebDriverWait(parent, timeoutInSecs);
            wait.until(expectedCondition);
        } catch (Exception e) {
            throw e;
        }
    }


    public String getAutObjectName() {
        return this.name;
    }
}
