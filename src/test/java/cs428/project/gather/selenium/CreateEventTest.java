package cs428.project.gather.selenium;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cs428.project.gather.GatherApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class CreateEventTest extends AbstractSeleniumTest{


 @BeforeClass
 public static void openBrowser(){
     
     driver = new FirefoxDriver();
     driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	} 

 @Test
 public void testCreateNewEvent(){
	 System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
	 driver.get("http://localhost:8888");
	 userSignIn();
	 driver.findElement(By.id("zipCode")).sendKeys("94704");
	 driver.findElement(By.id("enterZip")).click();
	 try {
		 Thread.sleep(2000);
	 } catch (InterruptedException e) {
		 e.printStackTrace();
	 }
	 driver.findElement(By.id("addEventBtn")).click();
	 WebDriverWait wait = new WebDriverWait(driver, 3);
	 WebElement updateEventInfoBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("updateEventInfoBtn")));
	 
	 updateEventInfoBtn.click();
	 
	 driver.findElement(By.id("event-name")).sendKeys("New Event");
	 driver.findElement(By.id("event-description")).sendKeys("this is a new event");
	 
	 Select select = new Select(driver.findElement(By.id("event-category")));
	 select.selectByValue("Soccer");
	 
	 DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	 Date today = new Date();
	 Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
	 driver.findElement(By.id("event-occurrence1")).sendKeys(dateFormat.format(tomorrow));
	 
	 driver.findElement(By.id("event-save")).click();
	 
	 Assert.assertTrue(driver.getPageSource().contains("this is a new event"));
	 
	 userSignOut();
	 System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
 }
 
 @AfterClass
 public static void closeBrowser(){
	driver.quit();
 }
}
