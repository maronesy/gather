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
public class UserRegistrationTest extends AbstractSeleniumTest {

 @BeforeClass
 public static void openBrowser(){
     
     driver = new FirefoxDriver();
     driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	} 
 
 @Test
 public void registrationTest()
 {
	 System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
     driver.get("http://localhost:8888");
     String regText = "Register";
	 driver.findElement (By.xpath("//*[contains(text(),'" + regText + "')]")).click();
     driver.findElement(By.id("inputEmail")).sendKeys("jackiechan@gmail.com");
     driver.findElement(By.id("inputPassword1")).sendKeys("password");
     driver.findElement(By.id("inputPassword2")).sendKeys("password");
     driver.findElement(By.id("inputDisplayName")).sendKeys("jackie");
     driver.findElement(By.id("registerFormSubmit")).click();
//     String subReg = "Submit";
//     driver.findElement (By.xpath("//*[contains(text(),'" + subReg + "')]")).click();
     try{
    	 String text="Welcome jackie";
    	 element = driver.findElement (By.xpath("//*[contains(text(),'" + text + "')]"));
	 }catch (Exception e){
		}
     Assert.assertNotNull(element);
     
     userSignOut();
     
     System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
 }

 @AfterClass
 public static void closeBrowser(){
	 driver.quit();
 }
}