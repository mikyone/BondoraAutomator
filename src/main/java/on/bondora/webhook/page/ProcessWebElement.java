package on.bondora.webhook.page;

import org.openqa.selenium.WebElement;

/**
 *
 *  
 * Created by onicolas on 10/04/2015.
 */
public interface ProcessWebElement<T> {

    /**
     * Convert a WebElement in T object.
     *
     * @param element
     * @return false or null means the element will be filtered.
     */
    T handleWebElement(WebElement element);

}
