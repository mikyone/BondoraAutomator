package on.bondora.webhook.page;

import com.google.common.base.Optional;
import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.*;



/**
 * <b>WATCH OUT </b>! This class has to be used with static page. Don't use it if the element(s) you want to find
 * has a chance to disappear dynamically.
 * <p>How it works !
 * <ul>
 *  <li>The constructor could be instantiated with an implementation of {@code ProcessWebElement}.</li>
 *  <li>Then the method  use the list of frames to find the autObject.</li>
 *  <li>If a {@code WebElement} is found, we call the method handleWebElement from the interface {@code ProcessWebElement} to process the {@code WebElement}.</li>
 * </ul>
 *
 *
 * <p>Created by onicolas on 10/04/2015.
 */
public class WebPageImpl<T> implements WebPage<T> {

    @SuppressWarnings("unchecked")
    private ProcessWebElement<T> processWebElement = (ProcessWebElement<T>) ProcessWebElementFactory.isWebElementPresent();
    private FramesTree framesTree;
    private int limit = 0;
    private Metrics metrics = new Metrics();
    protected final RemoteWebDriver parent;


    public WebPageImpl() {
        parent = DriverFactory.getDriverFactory().getRemoteWebDriver();
    }


    public WebPageImpl(ProcessWebElement<T> userProcessWebElement) {
        this();
        this.processWebElement = userProcessWebElement;
    }


    @Override
    public int getNumberFrames() {
        return getTree().getNbFrames();
    }

    @Override
    public void setProcessWebElement(ProcessWebElement<T> processWebElement) {
        this.processWebElement = processWebElement;
    }

    @Override
    public Optional<T> stopAfterFirstElementPresentInPage(AutObject object) {
        Map<AutObject, List<T>> elements = new Hashtable<>();
        final List<AutObject> objects = Collections.singletonList(object);
        limit(1).findElementsInPage(objects, elements);
        return elements.isEmpty() ? Optional.absent() : Optional.of(elements.entrySet().iterator().next().getValue().get(0));
    }


    @Override
    public List<T> findElementsInPage(AutObject object) {
        Map<AutObject, List<T>> elements = new Hashtable<>();
        final List<AutObject> objects = Collections.singletonList(object);
        findElementsInPage(objects, elements);
        return elements.isEmpty() ? new ArrayList<>() : elements.entrySet().iterator().next().getValue();
    }

    @Override
    public Optional<Pair<AutObject, T>> stopAfterFirstElementPresentInPage(List<AutObject> objects) {
        Map<AutObject, List<T>> elements = new Hashtable<>();
        limit(1).findElementsInPage(objects, elements);
        if (!elements.isEmpty()) {
            Map.Entry<AutObject, List<T>> entry = elements.entrySet().iterator().next();
            return Optional.of(new ImmutablePair<AutObject, T>(entry.getKey(), entry.getValue().get(0)));
        }
        return Optional.absent();

    }

    @Override
    public Map<AutObject, List<T>> findElementsInPage(List<AutObject> objects) {
        Map<AutObject, List<T>> elements = new Hashtable<>();
        findElementsInPage(objects, elements);
        return elements;
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }

    @Override
    public WebPageImpl limit(int limit) {
        this.limit = limit;
        return this;
    }

    protected FramesTree getTree() {
        if (framesTree == null) {
            framesTree = new FramesTree(null);
            framesTree.setNbFrames(fillFrameTreeRecursive(framesTree));
            System.out.println(framesTree.getNbFrames() + " :: frames found in the page.");
        }
        return framesTree;
    }


    private void switchToRootFrame() {
        parent.switchTo().defaultContent();
    }


    private Integer fillFrameTreeRecursive(FramesTree parentTree) {
        Integer numberFrames = Integer.valueOf(0);
        List<WebElement> iframes = findFrames();
        for (WebElement iframe : iframes) {
            FramesTree children = new FramesTree(iframe);
            children.setParent(parentTree);
            parentTree.getChild().add(children);
            parent.switchTo().frame(children.getCurrentWebElement());
            numberFrames += fillFrameTreeRecursive(children);
            parent.switchTo().parentFrame();
            numberFrames++;
        }
        return numberFrames;
    }

    private void findElementsInPage(List<AutObject> objects, Map<AutObject, List<T>> found) {
        metrics = new Metrics();
        switchToRootFrame();
        if (!findElementsUpdatedMap(objects, found)) {
            findElementsInFrames(objects, getTree().getChild(), found);
        }
        switchToRootFrame();

    }


    private boolean findElementsInFrames(List<AutObject> objects, List<FramesTree> child, Map<AutObject, List<T>> found) {
        for (FramesTree children : child) {
            parent.switchTo().frame(children.getCurrentWebElement());
            if (findElementsUpdatedMap(objects, found)) {
                return true;
            }
            if (findElementsInFrames(objects, children.getChild(), found)) {
                return true;
            }
            parent.switchTo().parentFrame();
        }
        return false;
    }

    private boolean findElementsUpdatedMap(List<AutObject> autObjects, Map<AutObject, List<T>> found) {
        for (AutObject object : autObjects) {
            List<WebElement> elements = findElementsBy(object.by);
            List<T> convertElements = converter(object, elements, found);

            if (!convertElements.isEmpty()) {
                updateMap(object, found, convertElements);
                if (isLimitReached(convertElements)) {
                    return true;
                }
            }
        }
        return false;
    }


    private List<T> converter(AutObject autObject, List<WebElement> elementsBy, Map<AutObject, List<T>> found) {
        List<T> elements = new ArrayList<T>();
        if (elementsBy.isEmpty()) {
            System.out.println(autObject.getAutObjectName() + " :: Element not found in child frame.");
        }
        // Process the Web Element.
        for (WebElement webElement : elementsBy) {
            T element = processWebElement.handleWebElement(webElement);
            if (element != null && !(element instanceof Boolean && element == Boolean.FALSE)) {
                metrics.setMatch(metrics.getMatch() + 1);
                System.out.println(autObject.getAutObjectName() + " :: Found element in child frame and stored it.");
                elements.add(element);
                if (isLimitReached(elements)) {
                    return elements;
                }
            } else {
                metrics.setNotMatch(metrics.getNotMatch() + 1);
                System.out.println(autObject.getAutObjectName() + " :: Found element in child frame but not handled .");
            }
        }
        return elements;
    }



    private void updateMap(AutObject object, Map<AutObject, List<T>> found, List<T> elements) {
        List<T> elementsStored = found.get(object);
        if (elementsStored == null) {
            found.put(object, elements);
        } else {
            elementsStored.addAll(elements);
            found.put(object, elementsStored);
        }
    }


    private List<WebElement> findFrames() {
        List<WebElement> frames = findElementsBy(By.tagName("iframe"));
        if (frames.isEmpty()) {
            frames = findElementsBy(By.tagName("frame"));
        }
        return frames;
    }

    private List<WebElement> findElementsBy(By by) {
        List<WebElement> matches = new ArrayList<>();
        try {
            matches = parent.findElements(by);
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
            //ignored fix for IE9
        }
        return matches;
    }

    private boolean isLimitReached(List<T> elements) {
        return limit > 0 && limit >= elements.size();
    }


    @Data
    static class FramesTree {
        private WebElement currentWebElement;
        private FramesTree parent;
        private List<FramesTree> child = new ArrayList<>();
        private Integer nbFrames;

        FramesTree(WebElement currentWebElement) {
            this.currentWebElement = currentWebElement;
        }

    }



}
