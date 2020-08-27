package on.bondora.webhook.page;

import lombok.Data;
import org.openqa.selenium.WebElement;

/**
 * Created by onicolas on 13/04/2015.
 */
public class ProcessWebElementFactory {


    /**
     * Return the WebElement without filter.
     *
     * @return
     */
    public static ProcessWebElement<Boolean> isWebElementPresent() {
        return new ProcessWebElement<Boolean>() {
            @Override
            public Boolean handleWebElement(WebElement element) {
                return true;
            }
        };
    }


    /**
     * Return only the  WebElement displayed.
     *
     * @return
     */
    public static ProcessWebElement<Boolean> isWebElementDisplayed() {
        return isWebElementDisplayed(true);
    }

    /**
     * Return only the  WebElement undisplayed.
     *
     * @return
     */
    public static ProcessWebElement<Boolean> isWebElementNotDisplayed() {
        return isWebElementDisplayed(false);
    }

    /**
     * Filter element if the element is matching with displayed parameter
     * @param displayed
     * @param displayed
     * @return
     */
    public static ProcessWebElement<Boolean> isWebElementDisplayed(final boolean displayed) {
        return new ProcessWebElement<Boolean>() {
            @Override
            public Boolean handleWebElement(WebElement element) {
                return element.isDisplayed() ==  displayed;
            }
        };
    }


    /**
     * Filter element if the element is matching with displayed and checked parameters
     * @param displayed
     * @param checked
     * @return
     */
    public static ProcessWebElement<Boolean> isSelectedDisplayedElement(final boolean displayed, final boolean checked) {
        return new ProcessWebElement<Boolean>() {
            @Override
            public Boolean handleWebElement(WebElement element) {
                return (element.isDisplayed() == displayed && element.isSelected() == checked);
            }
        };
    }

    /**
     * Convert WebElement  to SelectedDisplayedEnabled
     *
     * @return
     */
    public static ProcessWebElement<SelectedDisplayedEnabled> convertToSelectedDisplayedEnabled() {
        return new ProcessWebElement<SelectedDisplayedEnabled>() {
            @Override
            public SelectedDisplayedEnabled handleWebElement(WebElement element) {
                SelectedDisplayedEnabled selectedDisplayedEnabled = new SelectedDisplayedEnabled();
                selectedDisplayedEnabled.setDisplayed(element.isDisplayed());
                selectedDisplayedEnabled.setEnabled(element.isEnabled());
                selectedDisplayedEnabled.setSelected(element.isSelected());
                return selectedDisplayedEnabled;
            }
        };
    }

    /**
     * Convert WebElement displayed  to TagNameId
     *
     * @return
     */
    public static ProcessWebElement<TagNameId> convertToTagNameId() {
        return new ProcessWebElement<TagNameId>() {
            @Override
            public TagNameId handleWebElement(WebElement element) {
                if (element.isDisplayed()) {
                    TagNameId tagNameId = new TagNameId();
                    tagNameId.setTagName(element.getTagName());
                    tagNameId.setId(element.getAttribute("id"));
                    return tagNameId;
                }
                return null;
            }
        };
    }





    /**
     * Convert WebElement displayed  to TagNameValue
     *
     * @return
     */
    public static ProcessWebElement<TagNameValue> convertToTagNameValue(final String attribute) {

        return new ProcessWebElement<TagNameValue>() {
            @Override
            public TagNameValue handleWebElement(WebElement element) {
                if (element.isDisplayed()) {
                    TagNameValue tagNameValue = new TagNameValue();
                    tagNameValue.setTagName(element.getTagName());
                    tagNameValue.setValue(element.getAttribute(attribute));
                    return tagNameValue;
                }
                return null;
            }
        };
    }

    @Data
    public static class SelectedDisplayedEnabled {
        private boolean selected;
        private boolean enabled;
        private boolean displayed;

    }

    @Data
    public static class TagNameId {
        private String tagName;
        private String id;

    }

    @Data
    public static class TagNameText {
        private String tagName;
        private String text;

    }

    @Data
    public static class TagNameValue {
        private String tagName;
        private String value;

    }
}