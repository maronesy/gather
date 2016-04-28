package cs428.project.gather.selenium;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractSeleniumTest {

	protected static FirefoxDriver driver;
	protected WebElement element;
 	
 	protected void userSignOut() {
 		WebDriverWait wait = new WebDriverWait(driver, 3);
 		WebElement signOutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signOutButton")));
		signOutButton.click();
	    boolean elemHidden = isElementHidden(driver.findElement(By.id("loginFormSubmit")));
	    Assert.assertFalse(elemHidden);
	}

 	protected void userSignIn() {
	    
	    driver.findElement(By.id("signInEmail")).sendKeys("testuser@email.com");
	    driver.findElement(By.id("signInPassword")).sendKeys("password");
	    driver.findElement(By.id("loginFormSubmit")).click();

	    try{
	    	String text="Welcome testDisplayName";
	    	element = driver.findElement (By.xpath("//*[contains(text(),'" + text + "')]"));
		}catch (Exception e){
		}
	    Assert.assertNotNull(element);
	}
	

 	 protected void createNewEventWith(String name, String description, String category, int zipCode) {
 		driver.findElement(By.id("zipCode")).sendKeys(Integer.toString(zipCode));
 		 driver.findElement(By.id("enterZip")).click();
 		 this.timeoutFor(1500);
 		 driver.findElement(By.id("addEventBtn")).click();
 		 WebDriverWait wait = new WebDriverWait(driver, 3);
 		 WebElement updateEventInfoBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("updateEventInfoBtn")));
 		 
 		 updateEventInfoBtn.click();
 		 
 		 driver.findElement(By.id("event-name")).sendKeys(name);
 		 driver.findElement(By.id("event-description")).sendKeys(description);
 		 
 		 Select select = new Select(driver.findElement(By.id("event-category")));
 		 select.selectByValue(category);
 		 
 		 DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
 		 Date today = new Date();
 		 Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
 		 driver.findElement(By.id("event-occurrence1")).sendKeys(dateFormat.format(tomorrow));
 		 this.timeoutFor(1000);
 		 driver.findElement(By.id("event-save")).click();
 		 
 		 Assert.assertTrue(driver.getPageSource().contains(description));
 	 }
 	
 	protected boolean isElementHidden(WebElement welem){
		 boolean clickFailed=false;
	     try{
	    	 welem.click();
	     }catch(Exception e){
	    	 clickFailed = true; 
	     }
	     return clickFailed;
	 }
 	
 	protected void timeoutFor(int milliSec){
 		try {
 			 Thread.sleep(milliSec);
 		 } catch (InterruptedException e) {
 			 e.printStackTrace();
 		 }
 	}
}
