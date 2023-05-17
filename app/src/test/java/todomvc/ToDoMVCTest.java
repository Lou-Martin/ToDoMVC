package todomvc;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToDoMVCTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.get("https://todomvc.com/examples/react/#/");  // Navigate to the webpage before each test
        wait = new WebDriverWait(driver, Duration.ofSeconds(1));
    }

    public static void populateList(int numberOfItems) {
        WebElement newTodo = driver.findElement(By.className("new-todo")); //find blank list

        for(int i = 1; i <= numberOfItems; i++){
            String message = String.format("Test %s", i);
            newTodo.sendKeys(message, Keys.ENTER); //potentially add wait if helper method fails
            //Below break needs refactoring to reduce run times for large runs
            try {
                Thread.sleep(500); // Sleep for 500 milliseconds (half a second)
            } catch (InterruptedException e) {
                // Handle the exception, if necessary
            }
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

    //RUN BELOW OC OF TESTS
    @AfterAll
    public static void closeBrowser() {
        if (driver != null) driver.quit(); //close the browser after each test
    }

}
