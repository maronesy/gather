package cs428.project.gather.selenium;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public abstract class AbstractSeleniumTest {

	protected static FirefoxDriver driver;
	protected WebElement element;
 	
 	protected void userSignOut() {
		driver.findElement(By.id("signOutButton")).click();
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
}
