// Note: working to minimum viable product on these tests currently.
// Once all tests are implemented, plan to return and refactor for robustness and time efficiency

//Test branch

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToDoMVCTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Wait time purposefully set high as one tester has a known poor internet connection
        driver.get("https://todomvc.com/examples/react/#/");  // Navigate to the webpage before each test
        wait = new WebDriverWait(driver, Duration.ofMillis(500));
    }

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
    public void testEscapeModifyCancelsEdit(){
        populateList(1);
        WebElement todo1 = driver.findElement(By.xpath("//*[text()='Test 1']"));
        Actions act = new Actions(driver);
        act.doubleClick(todo1).perform();
        driver.switchTo().activeElement().sendKeys("EDITED TEST", Keys.ESCAPE);
        assertTrue(todo1.isDisplayed());
    }

    @Test
    public void testDeleteIncompleteToDoItem(){
        populateList(1);
        WebElement todo1 = driver.findElement(By.xpath("//*[text()='Test 1']"));
        Actions action = new Actions(driver);
        action.moveToElement(todo1).perform();
        WebElement deleteButton = driver.findElement(By.className("destroy"));
        wait.until(ExpectedConditions.visibilityOf(deleteButton));
        deleteButton.click();
        //code below generates a list of element(S) - useful for proving elements don't exist
        assertTrue(driver.findElements(By.xpath("//*[text()='Test 1']")).isEmpty());
    }

    @Test
    public void testDeleteCompleteToDoItem(){
        populateList(1);
        driver.findElement(By.className("toggle")).click();
        // add lines to assert item is completed
        WebElement deleteButton = driver.findElement(By.className("destroy"));
        wait.until(ExpectedConditions.visibilityOf(deleteButton));
        deleteButton.click();
        assertTrue(driver.findElements(By.xpath("//*[text()='Test 1']")).isEmpty());
    }

    @Test
    public void testToDoCountReturnsCorrectString(){
        populateList(1);
        assertTrue(driver.findElement(By.className("todo-count")).getText().contains("1 item left"));
        driver.findElement(By.className("toggle")).click();
        assertTrue(driver.findElement(By.className("todo-count")).getText().contains("0 items left"));
        populateList(2);
        assertTrue(driver.findElement(By.className("todo-count")).getText().contains("2 items left"));
        populateList(98);
        assertTrue(driver.findElement(By.className("todo-count")).getText().contains("100 items left"));

    }

    @Test
    public void testToDoCountNotDisplayedWithNoItems(){
        assertTrue(driver.findElements(By.className("todo-count")).isEmpty());
    }

    @Test
    public void testStatusBarToggle(){
        populateList(3);
        driver.findElement(By.className("toggle")).click();
        driver.findElement(By.linkText("Active")).click();
        //3 assertions in 1 test; when refactoring add exception handling or make potential failures explicit
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
    public void testCharacterLimit(){
        String message = "a".repeat(256);
        driver.findElement(By.className("new-todo")).sendKeys(message, Keys.ENTER);
        assertTrue(driver.findElement(By.xpath(String.format("//*[text()='%s']", message))).isDisplayed());
    }

    @Test
    public void testWordCount(){
        String message2 = "a ".repeat(128);
        driver.findElement(By.className("new-todo")).sendKeys(message2, Keys.ENTER);
        assertTrue(driver.findElement(By.xpath(String.format("//*[text()='%s']", message2.strip()))).isDisplayed());
    }

    @Test
    public void testClearCompleted(){
        populateList(1);
        driver.findElement(By.className("toggle")).click();
        assertTrue(driver.findElement(By.className("clear-completed")).isDisplayed());
        driver.findElement(By.className("clear-completed")).click();
        assertTrue(driver.findElements(By.xpath("//*[text()='Test 1']")).isEmpty());
    }

    @Test
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
    public void testDownArrowToggleAllComplete(){
        populateList(1);
        driver.findElement(By.className("toggle")).click();
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Active")).click();
        assertEquals(1, driver.findElements(By.className("view")).size());
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Completed")).click();
        assertEquals(1, driver.findElements(By.className("view")).size());
    }

    @Test
    public void testDownArrowToggleMix(){
        populateList(2);
        driver.findElement(By.className("toggle")).click();
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Completed")).click();
        assertEquals(2, driver.findElements(By.className("view")).size());
        driver.findElement(By.cssSelector(".main > label")).click();
        driver.findElement(By.linkText("Active")).click();
        assertEquals(2, driver.findElements(By.className("view")).size());
    }

    //RUN BELOW OC OF TESTS
    @AfterEach
    public void closeBrowser() {
        if (driver != null) driver.quit(); //close the browser after each test
    }

}
