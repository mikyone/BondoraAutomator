package on.bondora.webhook.page;

import com.google.common.base.Optional;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * WebPageImpl implemented this interface
 * Created by java on 09/04/2015.
 */
public interface WebPage<T> {


    /**
     * Return the number of frame/iframe in the current page
     *
     * @return the number of frames
     */
    int getNumberFrames();

    /**
     * Change the way of processing the WebElement.
     *
     * @param processWebElement
     */
    public void setProcessWebElement(ProcessWebElement<T> processWebElement);


    /**
     * Find a autObject element in the page and sub-frames
     * Stop the research after the first found.
     *
     * @param autObject
     *        The  {@code AutObject} to search for
     *
     * @return a {@code Optional} of a T element
     */
    Optional<T> stopAfterFirstElementPresentInPage(AutObject autObject) ;


    /**
     * From a {@code AutObject} we find all T elements in the page and sub-frames.
     *
     * @param autObject
     *        The {@code AutObject} to search for
     *
     * @return a {@code List} of the T element
     */
    List<T> findElementsInPage(AutObject autObject) ;


    /**
     * Get a map of {@code AutObject} associated with a list of T found in the page and sub-frames.
     * Stop the research after the first found.
     *
     * @param autObjects
     *        A list of  {@code AutObject} to search for
     *
     * @return a {@code Optional} of the T element associated to an the first {@code AutObject} found
     */
    Optional<Pair<AutObject, T>> stopAfterFirstElementPresentInPage(List<AutObject> autObjects) ;

    /**
     * Get a map of {@code AutObject} associated with a list of {@code WebElement} in the page and sub-frames.
     *
     * @param autObjects
     *        A list of  {@code AutObject} to search for
     *
     * @return a {@code Map} of T elements associated to {@code AutObject}
     */
    Map<AutObject, List<T>> findElementsInPage(List<AutObject> autObjects);


    /**
     * Return the metrics of the current page.
     * Basically return the number of elements filtered  and unfiltered.
     *
     * @return Metrics
     */
    Metrics getMetrics();


    /**
     * Set a limit to stop searching in the page if the limit is reached.
     * By default it's 0 that means no limit.
     */
    WebPage limit(int limit);

}

