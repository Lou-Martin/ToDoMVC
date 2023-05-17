package todomvc;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.testng.AssertJUnit.assertTrue;

public class ToDoMVCTest {
    private static ChromeDriver driver;
    @BeforeAll
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("https://todomvc.com/examples/react/#/");  // Navigate to the webpage before each test
    }


    //TEST CASES BELOW

    @Test
    public void testAddOneItem() {
        driver.findElement(By.className("new-todo")).sendKeys("Test 1", Keys.ENTER); //add one item to the list
        assertTrue(driver.findElement(By.className("todo-list")).getText().contains("Test 1"));  //check it exits
    }


    //RUN BELOW OC OF TESTS
    @AfterAll
    public static void closeBrowser() {
        if (driver != null) driver.quit(); //close the browser after each test
    }

}
