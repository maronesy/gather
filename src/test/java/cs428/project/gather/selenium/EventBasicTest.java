package cs428.project.gather.selenium;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cs428.project.gather.GatherApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class EventBasicTest extends AbstractSeleniumTest {

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
	 createNewEventWith("New Event", "this is a new event", "Soccer", 94704);
	 
	 userSignOut();
	 System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
 }
 
 @Test
 public void nearViewTest()
 {
	 System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
     driver.get("http://localhost:8888");
     
	 userSignIn();
	 
     String eText = "Welcome testDisplayName";
	 driver.findElement (By.xpath("//*[contains(text(),'" + eText + "')]")).click();
     driver.findElement(By.id("showNearBy")).click();
//     String subReg = "Submit";
//     driver.findElement (By.xpath("//*[contains(text(),'" + subReg + "')]")).click();
     try{
    	 String text="Nearby Event List";
    	 element = driver.findElement (By.xpath("//*[contains(text(),'" + text + "')]"));
	 }catch (Exception e){
		}
     Assert.assertNotNull(element);
     
     userSignOut();
     
     System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
 }
 
 @Test
 public void ownedViewTest()
 {
	 System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
     driver.get("http://localhost:8888");

	 userSignIn();
     
     String eText = "Welcome testDisplayName";
	 driver.findElement (By.xpath("//*[contains(text(),'" + eText + "')]")).click();
     driver.findElement(By.id("showOwned")).click();
//     String subReg = "Submit";
//     driver.findElement (By.xpath("//*[contains(text(),'" + subReg + "')]")).click();
     try{
    	 String text="Owned Event List";
    	 element = driver.findElement (By.xpath("//*[contains(text(),'" + text + "')]"));
	 }catch (Exception e){
		}
     Assert.assertNotNull(element);
     
     userSignOut();
     
     System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
 }
 
 @Test
 public void joinedViewTest()
 {
	 System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
     driver.get("http://localhost:8888");
     
     userSignIn();
     
     String eText = "Welcome testDisplayName";
	 driver.findElement (By.xpath("//*[contains(text(),'" + eText + "')]")).click();
     driver.findElement(By.id("showJoined")).click();
//     String subReg = "Submit";
//     driver.findElement (By.xpath("//*[contains(text(),'" + subReg + "')]")).click();
     try{
    	 String text="Joined Event List";
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