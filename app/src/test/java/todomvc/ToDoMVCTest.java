package todomvc;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ToDoMVCTest {

    private static WebDriver driver;

    @BeforeAll
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.get("https://todomvc.com/examples/react/#/");  // Navigate to the webpage before each test
    }

    public static void populateList(int numberOfItems) {
        WebElement newTodo = driver.findElement(By.className("new-todo")); //find blank list

        for(int i = 1; i <= numberOfItems; i++){
            String message = String.format("Test %s", i);
            newTodo.sendKeys(message, Keys.ENTER); //potentially add wait if helper method fails
        }
    }
    //TEST CASES BELOW

    //RUN BELOW OC OF TESTS
    @AfterAll
    public static void closeBrowser() {
        if (driver != null) driver.quit(); //close the browser after each test
    }

}
