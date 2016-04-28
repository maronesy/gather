package cs428.project.gather.selenium;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
