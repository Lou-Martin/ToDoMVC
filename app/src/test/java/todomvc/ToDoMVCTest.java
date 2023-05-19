package todomvc;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class ToDoMVCTest {
    //Initial set up for elements  used in multiple tests
    private static WebDriver driver;
    private static WebDriverWait wait;
    //Functions to find specified elements on page by given className
    public WebElement getToggleElement() {
        return driver.findElement(By.className("toggle"));
    }
    public WebElement getNewTodoElement() {
        return driver.findElement(By.className("new-todo"));
    }
    //Test set up to run before each test to ensure clean new session
    //Navigates to page in Chrome browser
    //Sets implicitlyWait and an explicit wait time
    //Had to manipulate these waits due to poor internet for one of the testers
    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        driver.get("https://todomvc.com/examples/react/#/");
        wait = new WebDriverWait(driver, Duration.ofMillis(500));
    }
    //Helper function to automate populating the list with items
    //Populates with n items 'Test #' where # is 1 to n
    //Done to give each item uniqueness in case need to find specific item
    //Requires wait to prevent race situations
    public static void populateList(int numberOfItems) {
        WebElement newTodo = driver.findElement(By.className("new-todo")); //find blank list
        for(int i = 1; i <= numberOfItems; i++){
            String message = String.format("Test %s", i);
            newTodo.sendKeys(message, Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format("//*[text()='%s']", message))));
        }
    }
    //TEST CASES BELOW
    @Test
    //Check can edit item with double click
    //Adds one item, double clicks it on screen, then adds additional text to it
    //Check final item reads as expected from edit
    public void modifyByDoubleClick(){
        populateList(1);
        WebElement todo1 = driver.findElement(By.xpath("//*[text()='Test 1']"));
        Actions act = new Actions(driver);
        act.doubleClick(todo1).perform();
        driver.switchTo().activeElement().sendKeys("EDITED TEST", Keys.ENTER);
        WebElement todo1Edited = driver.findElement(By.xpath("//*[text()='Test 1EDITED TEST']"));
        assertTrue(todo1Edited.isDisplayed());
    }

    @Test
    //Check editing item can be cancelled by returning ESCAPE
    //Item created, double clicked on, text added, then escape entered to cancel
    //Check final item has not changed from its initial state
    public void testEscapeModifyCancelsEdit(){
        populateList(1);
        WebElement todo1 = driver.findElement(By.xpath("//*[text()='Test 1']"));
        Actions act = new Actions(driver);
        act.doubleClick(todo1).perform();
        driver.switchTo().activeElement().sendKeys("EDITED TEST", Keys.ESCAPE);
        assertTrue(todo1.isDisplayed());
    }

    @Test
    //Check it is possible to delete an incomplete item
    //Item created - which will be incomplete - then mouse made to hover over delete button to reveal it, then clicked
    //Check that the item is no longer displayed
    public void testDeleteIncompleteToDoItem(){
        populateList(1);
        WebElement todo1 = driver.findElement(By.xpath("//*[text()='Test 1']"));
        Actions action = new Actions(driver);
        action.moveToElement(todo1).perform();
        WebElement deleteButton = driver.findElement(By.className("destroy"));
        wait.until(ExpectedConditions.visibilityOf(deleteButton));
        deleteButton.click();
        assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[text()='Test 1']"))));
    }

    @Test
    //Check it is possible to delete an complete item
    //Item created, is toggled to complete, then mouse made to hover over delete button to reveal it, then clicked
    //Check that the item is no longer displayed
    public void testDeleteCompleteToDoItem(){
        populateList(1);
        getToggleElement().click();
        WebElement deleteButton = driver.findElement(By.className("destroy"));
        wait.until(ExpectedConditions.visibilityOf(deleteButton));
        deleteButton.click();
        assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[text()='Test 1']"))));
    }

    @Test
    //Check that the count at the bottom of the list displays correct value
    //Populate initially with one item to test "1 item left"
    //Toggle that item to complete to test "0 items left" as status bar hidden it list is empty
    //Populate additional 2 items to test "2 items left"
    //Populate additional 98 items to test large number of items supported
    //100 chosen to check if display up to 3 digits
    //Note reusing populateList multiple times would result in multiple "Test 1" and "Test 2"
    //This is not an issue as this test does not depend on uniqueness
    public void testToDoCountReturnsCorrectString(){
        populateList(1);
        assertTrue(driver.findElement(By.className("todo-count")).getText().contains("1 item left"));
        getToggleElement().click();
        assertTrue(driver.findElement(By.className("todo-count")).getText().contains("0 items left"));
        populateList(2);
        assertTrue(driver.findElement(By.className("todo-count")).getText().contains("2 items left"));
        populateList(98);
        assertTrue(driver.findElement(By.className("todo-count")).getText().contains("100 items left"));
    }

    @Test
    //Check that the count is not displayed when no items (complete or incomplete) are the list
    public void testToDoCountNotDisplayedWithNoItems(){
        assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("todo-count"))));
    }

    @Test
    //Check that status bar toggles allow sorting between all, completed, and active
    //Populate 3 items and toggle one to complete
    //This allows easy checking between number complete and number active (1 and 2 respectively)
    //Click toggles to move between active, completed, and all lists
    //Check each list size is as expected (2, 1 and 3 respectively)
    public void testStatusBarToggle(){
        populateList(3);
        getToggleElement().click();
        driver.findElement(By.linkText("Active")).click();
        assertEquals(2, driver.findElements(By.className("view")).size());
        driver.findElement(By.linkText("Completed")).click();
        assertEquals(1, driver.findElements(By.className("view")).size());
        driver.findElement(By.linkText("All")).click();
        assertEquals(3, driver.findElements(By.className("view")).size());

    }

    //Below test is an alternate version of above that checks the url instead of page content.
    @Test
    public void testStatusBarURL() {
        populateList(1);
        driver.findElement(By.linkText("Active")).click();
        //3 assertions in 1 test; when refactoring add exception handling or make potential failures explicit
        assertEquals("https://todomvc.com/examples/react/#/active", driver.getCurrentUrl());
        driver.findElement(By.linkText("Completed")).click();
        assertEquals("https://todomvc.com/examples/react/#/completed", driver.getCurrentUrl());
        driver.findElement(By.linkText("All")).click();
        assertEquals("https://todomvc.com/examples/react/#/", driver.getCurrentUrl());

    }

    @Test
    //Check character limit on input item
    //From exploratory testing know this to actually be 65536 (2**16)
    //In practical use decided to only test 256 as for most users this would be more than enough
    //Add a new item of 256 'a's
    //Check displayed item matches expected
    public void testCharacterLimit(){
        String message = "a".repeat(256);
        getNewTodoElement().sendKeys(message, Keys.ENTER);
        assertTrue(driver.findElement(By.xpath(String.format("//*[text()='%s']", message))).isDisplayed());
    }

    @Test
    //Additional test not in original plan
    //Check word limit on input item
    //Again number is arbitrary, decided 128 should meet most users needs for purpose of this exercise
    //Add new item as a string on 128 'a 's
    //Check displayed item matches expected
    //Note need to strip trailing white space as ToDoMVC does this on displayed items
    public void testWordCount(){
        String message2 = "a ".repeat(128);
        getNewTodoElement().sendKeys(message2, Keys.ENTER);
        assertTrue(driver.findElement(By.xpath(String.format("//*[text()='%s']", message2.strip()))).isDisplayed());
    }

    @Test
    //Check clear completed correctly removes completed items
    //Populate list with one item, toggle to complete, then click clear completed.
    //Check item is no longer displayed
    public void testClearCompleted(){
        populateList(1);
        getToggleElement().click();
        assertTrue(driver.findElement(By.className("clear-completed")).isDisplayed());
        driver.findElement(By.className("clear-completed")).click();
        assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[text()='Test 1']"))));
    }

    @Test
    //Check down arrow toggles all items from incomplete to complete, then back to incomplete
    //Populate one item (initialy incomplete), click the down arrow to mark complete, then move to completed list
    //Check check that list has an item in it
    //Repeat moving from complete to incomplete
    public void testDownArrowToggleAllIncomplete(){
        populateList(1);
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Completed")).click();
        assertEquals(1, driver.findElements(By.className("view")).size());
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Active")).click();
        assertEquals(1, driver.findElements(By.className("view")).size());
    }

    @Test
    //Check down arrow toggles an initially complete item to incomplete, then back to complete
    //As previous test, though toggling newly created item to complete before using the down arrow
    public void testDownArrowToggleAllComplete(){
        populateList(1);
        getToggleElement().click();
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Active")).click();
        assertEquals(1, driver.findElements(By.className("view")).size());
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Completed")).click();
        assertEquals(1, driver.findElements(By.className("view")).size());
    }

    @Test
    //Check that the down test arrow behaves as expected when there is initially a mix of complete and incomplete items
    //Create two items, and toggle one to complete
    //Click down arrow, the move to completed tab
    //Check two items in list
    //Repeat, moving to active to check all items not marked incomplete
    public void testDownArrowToggleMix(){
        populateList(2);
        getToggleElement().click();
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Completed")).click();
        assertEquals(2, driver.findElements(By.className("view")).size());
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Active")).click();
        assertEquals(2, driver.findElements(By.className("view")).size());
    }

    //RUN BELOW OC OF TESTS
    @AfterEach
    //After each test close the browser to ensure each test starts in new state
    public void closeBrowser() {
        if (driver != null) driver.quit(); //close the browser after each test
    }

}
