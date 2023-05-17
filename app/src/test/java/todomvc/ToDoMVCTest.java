package todomvc;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

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
        //add one item to the list
        //check it exits
    }


    //RUN BELOW OC OF TESTS
    @AfterAll
    public static void closeBrowser() {
        if (driver != null) driver.quit(); //close the browser after each test
    }

}
