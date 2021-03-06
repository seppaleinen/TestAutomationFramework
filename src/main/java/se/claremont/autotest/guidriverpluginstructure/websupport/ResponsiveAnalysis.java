package se.claremont.autotest.guidriverpluginstructure.websupport;

import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.claremont.autotest.common.LogLevel;
import se.claremont.autotest.common.TestCase;
import se.claremont.autotest.support.StringManagement;
import se.claremont.autotest.support.SupportMethods;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordam on 2016-10-15.
 */
public class ResponsiveAnalysis {

    private final static Logger logger = LoggerFactory.getLogger( ResponsiveAnalysis.class );

    private List<ResolutionAssessment> elementCollections = new ArrayList<>();
    //List<CompareElement> oddElements = new ArrayList<>();
    private TestCase testCase;
    private WebDriver driver;
    private List<Dimension> resolutions;

    public ResponsiveAnalysis(WebDriver driver, List<Dimension> resolutions, TestCase testCase){
        this.testCase = testCase;
        this.driver = driver;
        this.resolutions = resolutions;
    }

    public void performAnalysisAndReportResults(){
        testCase.log(LogLevel.DEBUG, "Initiating responsive web analysis.");
        reportRunProblemsIfIdentified();
        if(!isRunnable()) return;
        createElementCollections();
        compareElementCollections();
        //buildResolutionAssessmentOddElementCollections();
        //report();
    }

    private boolean isRunnable(){
        return !(testCase == null || resolutions.size() < 2 || driver == null);
    }

    private void reportRunProblemsIfIdentified(){
        if(testCase == null) {
            logger.debug( "Cannot log results for web analysis since no test case is given. Aborting." );
        }
        if(resolutions.size() < 2) testCase.log(LogLevel.EXECUTION_PROBLEM, "Cannot compare resolutions if only one resolution is entered.");
        if(driver == null) testCase.log(LogLevel.EXECUTION_PROBLEM, "Cannot assess responsive web analysis for driver that is null.");
    }

    //If innerHTML of children differs, try child elements to identify exact one
    //what if the last collection has elements displayed that are not displayed in earlier collections?
    private void compareElementCollections(){
        List<ReportableElement> deviatingElementsList = new ArrayList<>();
        for(int i=0; i < elementCollections.size()-1 ; i++){
            for(AssessmentElement assessmentElement1 : elementCollections.get(i).displayedElementsTreeRootNode.toListOfAssessmentElementsIncludingSubtree()){
                boolean elementMatchFound = false;
                for(int j=i+1; j < elementCollections.size();j++){
                    if(elementCollections.get(j).containElement(assessmentElement1)){
                        elementMatchFound = true;
                        break;
                    }
                }
                if(!elementMatchFound){
                    deviatingElementsList.add(new ReportableElement(assessmentElement1, elementCollections.get(i)));
                }
            }
        }
        for(ReportableElement deviatingElement : deviatingElementsList){
            testCase.log(LogLevel.INFO, "Found deviating element with innerHTML '" + deviatingElement.assessmentElement.innerHtml + "'.");
        }
    }

    private boolean setBrowserSizeAndReturnTrueIfSuccessful(Dimension browserSize){
        try{
            testCase.log(LogLevel.DEBUG, "Setting screen resolution to " + browserSize.width + "x" + browserSize.height + ".");
            driver.manage().window().setSize(browserSize);
        }catch (Exception e1){
            testCase.log(LogLevel.EXECUTION_PROBLEM, "Could not set resolution to " + browserSize.width + "x" + browserSize.height + ". " + e1.getMessage());
            return false;
        }
        return true;
    }

    private File getScreenshot(){
        File returnImage = null;
        try {
            testCase.log(LogLevel.DEBUG, "Capturing screenshot of web page.");
            returnImage = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        }catch (Exception e2){
            testCase.log(LogLevel.EXECUTION_PROBLEM, "Could not take screenshot of web page. " + e2.getMessage());
        }
        return returnImage;
    }

    private void compareCollections() {
        List<AssessmentElement> deviatingElements = new ArrayList<>();
        for(int i = 0; i < elementCollections.size() -1 ; i++){
            for(int j = i+1; j <elementCollections.size() ;j++){
            }
        }
    }
    private void tryCollectVisibleElements(ResolutionAssessment resolutionAssessment){
        try {
            testCase.log(LogLevel.DEBUG, "Collecting visible web elements in resolution " + resolutionAssessment.resolution.width + "x" + resolutionAssessment.resolution.height + ".");
            collectVisibleElements(resolutionAssessment);
            testCase.log(LogLevel.INFO, resolutionAssessment.toString());
        }catch (Exception e3){
            testCase.log(LogLevel.EXECUTION_PROBLEM, "Something went wrong while trying to collect elements for resolution " + resolutionAssessment.resolution.width + "x" + resolutionAssessment.resolution.height + ". " + e3.getMessage());
        }
    }

    private void createElementCollections(){
        for(Dimension resolution : resolutions){
            ResolutionAssessment resolutionAssessment = new ResolutionAssessment();
            resolutionAssessment.resolution = resolution;
            testCase.log(LogLevel.DEBUG, "Creating element collection for resolution with width=" + resolution.width + " and height=" + resolution.height + ".");
            if(!setBrowserSizeAndReturnTrueIfSuccessful(resolution)) return;
            resolutionAssessment.screenImage = getScreenshot();
            tryCollectVisibleElements(resolutionAssessment);
            testCase.log(LogLevel.DEBUG, "Identified " + resolutionAssessment.displayedElementsTreeRootNode.childrenInSubTreeCount() + " visible elements.");
        }
    }

    public void saveImage(AssessmentElement element, String filePath, File screenImage){
        BufferedImage img = null;
        BufferedImage dest = null;
        try {
            img = ImageIO.read(screenImage);
        } catch (IOException e) {
            testCase.log(LogLevel.EXECUTION_PROBLEM, "Could not read screenshot of full screenshot when trying to capture an image.");
            return;
        }
        try {
            dest = img.getSubimage(element.position.getX(), element.position.getY(), element.size.width, element.size.height); //cut Image using height, width and x y coordinates parameters.
        }catch (Exception e){
            testCase.log(LogLevel.EXECUTION_PROBLEM, "Could nog crop browser window to element.");
            return;
        }
        try {
            ImageIO.write(dest, "png", new File(filePath));
        } catch (IOException e) {
            testCase.log(LogLevel.EXECUTION_PROBLEM, "Could not write image to file '" + filePath + "'.");
        }
    }

    private void collectVisibleElements(ResolutionAssessment resolutionAssessment){
        resolutionAssessment.displayedElementsTreeRootNode = new AssessmentElement(driver.findElement(By.xpath("//body")));
        elementCollections.add(resolutionAssessment);
    }

    class AssessmentElement{
        Dimension size = new Dimension(0,0);
        Point position = new Point(0,0);
        WebElement element;
        String innerHtml = "";
        List<WebElement> immediateChildren = new ArrayList<>();
        List<AssessmentElement> assessmentChildElements = new ArrayList<>();

        AssessmentElement(WebElement webElement){
            element = webElement;
            immediateChildren.addAll(webElement.findElements(By.xpath("*")));
            innerHtml = element.getAttribute("innerHTML");
            try {
                this.size = webElement.getSize();
            }catch (Exception ignored1){}
            try {
                this.position = webElement.getLocation();
            }catch (Exception ignored3){}
            try {
                this.innerHtml = webElement.getAttribute("innerHTML");
                logger.debug( innerHtml );
            }catch (Exception ignored4){}
            for(WebElement child : immediateChildren){
                try {
                    if(child.isDisplayed()) assessmentChildElements.add(new AssessmentElement(child));
                }catch (Exception ignored){} //Stale element
            }
            logger.debug( this.toString() );
        }

        public @Override String toString(){
            return "[Visible immediate children count: " + assessmentChildElements.size() + ", hidden immediate children count: " + (immediateChildren.size() - assessmentChildElements.size()) + ", size: " + size.width + "x" + size.height + ", position: " + position.getX() + "x" + position.getY() + ", innerHTML: '" + StringManagement.htmlContentToDisplayableHtmlCode(innerHtml) + "']";
        }

        boolean innerHtmlIsSame(AssessmentElement assessmentElement){
            return innerHtml.equals(assessmentElement.innerHtml);
        }

        private List<AssessmentElement> getDeviatingChildren(List<AssessmentElement> oddElements, List<AssessmentElement> foreignOddElements){
            List<AssessmentElement> returnObjectList = new ArrayList<>();
            for(AssessmentElement a1 : oddElements){
                boolean matchInSubElementFound = false;
                for(AssessmentElement a1Child : a1.assessmentChildElements ){
                    for(AssessmentElement a2 : foreignOddElements){
                        for(AssessmentElement a2Child : a2.assessmentChildElements){
                            if(a1Child.innerHtmlIsSame(a2Child)){
                                matchInSubElementFound = true; //Some child elements are the same -> This is not the atomic differenting node.
                                break;
                            }
                        }
                        if(matchInSubElementFound){
                            break;
                        } else {
                            returnObjectList.addAll(getChildrenWithDeviatingInnerHtmlFromChildElementsOfRootElement(a1));
                        }
                    }
                }
            }
            return returnObjectList;
        }


        List<AssessmentElement> getChildrenWithDeviatingInnerHtmlFromChildElementsOfRootElement(AssessmentElement assessmentElement){
            List<AssessmentElement> returnObjectList = new ArrayList<>();
            List<AssessmentElement> oddElements = new ArrayList<>();
            List<AssessmentElement> foreignOddElements = new ArrayList<>();
            for(AssessmentElement child : assessmentChildElements){
                boolean innerHtmlMatchFound = false;
                for(AssessmentElement compareChild : assessmentElement.assessmentChildElements){
                    if(child.innerHtml.equals(compareChild.innerHtml) && child.size.equals(compareChild.size)){
                        innerHtmlMatchFound = true;
                        break;
                    }
                }
                if(!innerHtmlMatchFound) {
                    returnObjectList.addAll(getDeviatingChildren(oddElements, foreignOddElements));
                }
            }
            return returnObjectList;
        }

        int immediateChildrenCount(){
            return assessmentChildElements.size();
        }

        int childrenInSubTreeCount(){
            int count = immediateChildrenCount();
            for(AssessmentElement e : assessmentChildElements){
                count += e.childrenInSubTreeCount();
            }
            return count;
        }

        String allChildrenInSubTreeToString(){
            String text = this.toString() + SupportMethods.LF;
            for(AssessmentElement e : assessmentChildElements){
                text += e.allChildrenInSubTreeToString();
            }
            return text;
        }

        boolean hasSameElementInSubtreeOf(AssessmentElement assessmentElement){
            boolean found = false;
            if(this.isEqualTo(assessmentElement)) return true;
            for(AssessmentElement e : assessmentChildElements){
                found = hasSameElementInSubtreeOf(e);
            }
            return found;
        }

        List<AssessmentElement> toListOfAssessmentElementsIncludingSubtree(){
            List<AssessmentElement> returnList = new ArrayList<>();
            returnList.add(this);
            for(AssessmentElement e : assessmentChildElements){
                returnList.addAll(e.toListOfAssessmentElementsIncludingSubtree());
            }
            return returnList;
        }

        public boolean hasSameElementRescaledInSubtreeOf(AssessmentElement assessmentElement){
            boolean found = false;
            if(this.isEqualToButRescaledComparedTo(assessmentElement)) return true;
            for(AssessmentElement e : assessmentChildElements){
                found = isEqualToButRescaledComparedTo(e);
            }
            return found;
        }

        boolean isEqualToButRescaledComparedTo(AssessmentElement assessmentElement){
            return (size != assessmentElement.size && innerHtml.equals(assessmentElement.innerHtml));
        }

        public boolean isFoundUnchangedAmongChildElements(AssessmentElement assessmentElement){
            for(AssessmentElement childElement : assessmentChildElements){
                if(assessmentElement.isEqualTo(childElement)) return true;
            }
            return false;
        }

        public boolean isFoundButRescaledAmongChildElements(AssessmentElement assessmentElement){
            for(AssessmentElement child : assessmentChildElements){
                if(assessmentElement.isEqualToButRescaledComparedTo(child)) return true;
            }
            return false;
        }

        boolean isEqualTo(AssessmentElement assessmentElement){
            return (innerHtml.equals(assessmentElement.innerHtml) && size.equals(assessmentElement.size));
        }
    }

    class ReportableElement{
        ResolutionAssessment resolutionAssessments;
        AssessmentElement assessmentElement;

        ReportableElement(AssessmentElement assessmentElement, ResolutionAssessment resolutionAssessment){
            this.assessmentElement = assessmentElement;
            this.resolutionAssessments = resolutionAssessment;
        }
    }

    class ResolutionAssessment{
        Dimension resolution;
        File screenImage;
        AssessmentElement displayedElementsTreeRootNode;

        public List<AssessmentElement> getDifferences(ResolutionAssessment resolutionAssessment){
            List<AssessmentElement> deviatingElementsList = new ArrayList<>();
            for(int i=0; i < elementCollections.size()-1 ; i++){
                for(int j=i+1; j < elementCollections.size();j++){
                    deviatingElementsList.addAll(elementCollections.get(i).displayedElementsTreeRootNode.getChildrenWithDeviatingInnerHtmlFromChildElementsOfRootElement(elementCollections.get(j).displayedElementsTreeRootNode));
                }
            }
            for(AssessmentElement deviatingElement : deviatingElementsList){
                testCase.log(LogLevel.INFO, "Found deviating element with innerHTML '" + deviatingElement.innerHtml + "'.");
            }
            return deviatingElementsList;
        }

        public @Override String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append("ResolutionAssessment: Resolution = ").append(resolution.width).append("x").append(resolution.height).append(SupportMethods.LF);
            sb.append(displayedElementsTreeRootNode.allChildrenInSubTreeToString()).append(SupportMethods.LF);
            return sb.toString();
        }

        boolean containElement(AssessmentElement assessmentElement){
            for(AssessmentElement assessmentElement2 : this.displayedElementsTreeRootNode.toListOfAssessmentElementsIncludingSubtree()){
                if(assessmentElement.isEqualTo(assessmentElement2)) {
                    return true;
                }
            }
            return false;
        }

    }
}
