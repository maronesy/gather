package cs428.project.gather.selenium;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cs428.project.gather.GatherApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class UserLogoutTest {
	private static FirefoxDriver driver;
 	WebElement element;

 @BeforeClass
 public static void openBrowser(){
     
     driver = new FirefoxDriver();
     driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	} 

 @Test
 public void logoutTest()
 {
	 System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
     driver.get("http://localhost:8888");	
     driver.findElement(By.id("signInEmail")).sendKeys("testuser@email.com");
     driver.findElement(By.id("signInPassword")).sendKeys("password");
     driver.findElement(By.id("loginFormSubmit")).click();
     driver.findElement(By.id("signOutButton")).click();
//     String outText = "Sign Out";
//	 driver.findElement (By.xpath("//*[contains(text(),'" + outText + "')]")).click();
     try{
		 //element = driver.findElement (By.xpath(".//*[@id='loginErrorBox']/a"));
    	 String text="Sign In";
    	 element = driver.findElement (By.xpath("//*[contains(text(),'" + text + "')]"));
	 }catch (Exception e){
		}
     Assert.assertNotNull(element);
     System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
 }

 @AfterClass
 public static void closeBrowser(){
	 driver.quit();
 }
}
