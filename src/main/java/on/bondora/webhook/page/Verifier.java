package on.bondora.webhook.page;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by onicolas on 20/04/2015.
 */
public class Verifier {

    private final List<AutObject>autObjects ;
    private WebPage webPage;

    private Verifier(List<AutObject> autObjects) {
        this.autObjects = autObjects;
    }


    public static Verifier elements(List<AutObject>autObjects){
        return new Verifier(autObjects);
    }

    public static Verifier element(AutObject autObjects){
        return new Verifier(Arrays.asList(autObjects));
    }


    /**
     * Assert that all the AutObject have to be selected
     */
    public Expectation haveToBeDisplayed(){
        return new Expectation(){
            @Override
            public void assertion() {
                Metrics metrics =  matchDisplayedElement(true);
                CustomAssertion.customAssertTrue(metrics.getNotMatch() == 0, " All the AutObjects have to be displayed ");
            }
        };
    }


    /**
     * Assert that all the AutObject have to be selected
     */
    public Expectation haveToBeNotDisplayed(){
        return new Expectation() {
            @Override
            public void assertion() {
                Metrics metrics =  matchDisplayedElement(false);
                CustomAssertion.customAssertTrue(metrics.getNotMatch() == 0, " All the AutObjects have to be  not displayed ");
            }
        };



    }

    /**
     * Assert that all the AutObject have to be selected
     */
    public Expectation haveToBeSelected(){
        return new Expectation() {
            @Override
            public void assertion() {
                Metrics metrics = getMetricsFromSelected();
                CustomAssertion.customAssertTrue(metrics.getNotMatch() == 0, " All the AutObjects have to be selected ");
            }
        };

    }

    /**
     * Assert that all the AutObject have to be unselected
     */
    public Expectation haveToBeUnSelected(){
        return new Expectation(){
            @Override
            public void assertion() {
                Metrics metrics = getMetricsFromUnSelected();
                CustomAssertion.customAssertTrue(metrics.getNotMatch() == 0, " All the AutObjects have to be unselected ");
            }
        };
    }


    /**
     * Assert that all the AutObject have to be unselected
     */
    public Expectation displayedHaveToBeEquals(int nbFound){
        return new Expectation(){
            @Override
            public void assertion() {
                Metrics metrics =  matchDisplayedElement(true);
                CustomAssertion.customAssertTrue(metrics.getMatch() == nbFound, " Number AutObjects found is not equals to the expectation (" + metrics.getMatch() + "<>" + nbFound+")");
            }
        };
    }

    /**
     * Assert that all the AutObject have to be unselected
     */
    public Expectation selectedHaveToBeEquals(int nbFound){
        return new Expectation(){
            @Override
            public void assertion() {
                Metrics metrics = getMetricsFromSelected();
                CustomAssertion.customAssertTrue(metrics.getMatch() == nbFound, " Number AutObjects found is not equals to the expectation (" + metrics.getMatch() + "<>" + nbFound+")");
            }
        };
    }



    private Metrics getMetricsFromSelected(){
        boolean displayed = true, selected = true;
        return matchSelectedDisplayedElement(displayed, selected);
    }


    private Metrics getMetricsFromUnSelected(){
        boolean displayed = true, selected = false;
        return matchSelectedDisplayedElement(displayed, selected);
    }


    /**
     * Instantiate the WebPage or change is WebElement processor
     *
     * @param processWebElement
     * @param <T>
     * @return
     */
    private <T> WebPage<T> getWebPage(ProcessWebElement<T>processWebElement){
        if (webPage == null){
            this.webPage = new WebPageImpl<>(processWebElement);
        }else{
            this.webPage.setProcessWebElement(processWebElement);
        }
        return webPage;
    }

    /**
     * get the elements which are only displayed
     * @param displayed
     * @return
     */
    private Metrics matchDisplayedElement(boolean displayed){
        ProcessWebElement<Boolean> unselectedCheckBox = ProcessWebElementFactory.isWebElementDisplayed(displayed);
        WebPage<Boolean> webPage = getWebPage(unselectedCheckBox);
        Map<AutObject, List<Boolean>> elementsInPage = webPage.findElementsInPage(autObjects);
        String isDisplayed =  displayed ? "displayed":"undisplayed";
        report(webPage.getMetrics(), elementsInPage, isDisplayed);
        return webPage.getMetrics();

    }


    /**
     * get the elements which are displayed and selected
     * @param displayed
     * @param selected
     * @return
     */
    private Metrics matchSelectedDisplayedElement(boolean displayed, boolean selected){
        ProcessWebElement<Boolean> unselectedCheckBox = ProcessWebElementFactory.isSelectedDisplayedElement(displayed, selected);
        WebPage<Boolean> webPage = getWebPage(unselectedCheckBox);
        Map<AutObject, List<Boolean>> elementsInPage = webPage.findElementsInPage(autObjects);
        String isDisplayed =  displayed ? "displayed":"undisplayed";
        String isSelected = " and " + (selected ? "selected":"unselected");
        report(webPage.getMetrics(), elementsInPage, isDisplayed, isSelected);
        return webPage.getMetrics();
        
    }

    /**
     * Get a log report
     * @param metrics
     * @param elementsInPage
     * @param moreLogs
     */
    private void report(Metrics metrics, Map<AutObject, List<Boolean>> elementsInPage, String...moreLogs) {
        System.out.println(metrics.getMatch()+ " WebElement match with your condition " + metrics.getNotMatch()+ " WebElement  are filtered");

        for(Map.Entry<AutObject, List<Boolean>> entry : elementsInPage.entrySet()){
            System.out.println("AutObject "  + entry.getKey().getAutObjectName()+ " is " + StringUtils.join(moreLogs) + " in the page");
        }
    }


}
