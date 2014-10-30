import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.firefox.FirefoxDriver;

/*
 * Program texts any Koodo Mobile # using http://koodomobile.com/en/content/sendamessageInner.html
 * User indicates time interval (in minutes) in which they would like the program to 
 * text a specified cellphone number
 */

class NewThread implements Runnable{
	Random rand = new Random();
	
    final long START_TIME = System.currentTimeMillis();
    public static long totalTimeMinutes = 0;
    long totalTimeMillis = 0;
    //final long INTERVAL_TIME = 300000;
    final String baseURL = "http://koodomobile.com/en/content/sendamessageInner.html";
    String areaCode = "";
    String phoneNum = "";
    int minSend = 0;
    int maxSend = 0;
    
    Thread t;
    
    NewThread(String areaCode,String phoneNum,int minSend,int maxSend, long totalTimeMinutes){
        this.areaCode = areaCode;
        this.phoneNum = phoneNum;
        this.minSend = minSend;
        this.maxSend = maxSend; // Add 1 because Random.nextInt() picks numbers exclusive of the upper range
        this.totalTimeMillis = TimeUnit.MINUTES.toMillis(totalTimeMinutes);
        t = new Thread(this, "Demo thread");
        // System.out.println("Child thread " + t); // test output
        t.start();
    }
    
    // String array of messages
    String[] messagesList = new String[]{
    	"Hello",
    	"This is not spam! :-)",
    	"Good day to you sir/madam!",
    	"Congrats! You have won a cruise for two to anywhere in the world! "
    			+ "Just kidding. I hope you're not too disappointed.",
    	};    
    
    public void run(){
        WebDriver driver = new FirefoxDriver();
        int randArg = maxSend - minSend + 1;
        int intervalTime = 0;
        int intervalMillis = 0; // intervalTime will be converted to this milliseconds for input into sleep()
        int totTexts = 1;
        try{
           
            while((System.currentTimeMillis() - START_TIME) < totalTimeMillis){
            	// Variable to select random message from list
            	int whichMsg = rand.nextInt(messagesList.length);
            	System.out.println("Picked message #" + (whichMsg + 1) + " from list: " + messagesList[whichMsg]);
                sendText(driver, baseURL, areaCode, phoneNum, messagesList[whichMsg]);
                //System.out.println("Sent at " + System.currentTimeMillis()); // Test output
                System.out.println("Total texts sent: " + totTexts);
                System.out.println();
                totTexts++;
                 
                intervalTime = rand.nextInt(randArg) + minSend; // Pick random time between interval range to wait until
                												// next text is sent
                intervalMillis = millisToMin(intervalTime);
                System.out.println("Interval time for current run: " + intervalTime);
                Thread.sleep(intervalMillis);
            } // end while
        } catch(InterruptedException e){
            //System.out.println("Child thread interrupted"); // test output
        }
        // System.out.println("Child thread exiting."); // test output
        driver.close(); // Close browser
        System.exit(0); // Explicitly exit program
    }
    
    public static void sendText(WebDriver d,String url,String areaNum,String phoneNo,String msg){
        d.get(url);       
        d.findElement(By.name("CODE")).sendKeys(areaNum);
        d.findElement(By.name("NUM")).sendKeys(phoneNo);
        d.findElement(By.name("MESSAGE")).sendKeys(msg);
        d.findElement(By.xpath("/html/body/form/table[3]/tbody/tr/td[2]/a")).click();
    }
    
    public static int millisToMin(int minutes){
    	int millis = 0;
    	millis = minutes * 60 * 1000;
    	return millis;
    }
}

public class TrollTexter {
    public static void main(String[] args){ 	    	
    	// Max length for text fields and text areas
    	// as determined by Koodo website
    	final int AREA_LENGTH = 3;
    	final int PHONE_LENGTH = 7;
    	final int MAX_MESSAGE_LENGTH = 140;
    	final int MIN_MESSAGE_LENGTH = 0;
    	
    	Scanner in = new Scanner(System.in);
    	String area = "";
        String phoneNo = "";
        //String msg = "";
        int minInterval = 0;
    	int maxInterval = 0;
    	
    	
    	do{
    		System.out.println("Enter area code: ");
    		area = in.nextLine();
    		if(area.length() != AREA_LENGTH)
    			System.out.println("Area code must be 3 numbers. Please try again.");
    	}while(area.length() != AREA_LENGTH);

    	do{
    		System.out.println("Enter remainder of phone number (with no '-'): ");
    		phoneNo = in.nextLine();
    		if(phoneNo.length() != PHONE_LENGTH)
    			System.out.println("Phone number must be 7 numbers. Please try again.");
    	}while(phoneNo.length() != PHONE_LENGTH);
    	
    	System.out.println("Enter minimum interval time (in minutes) for sending texts: ");
    	minInterval = in.nextInt();
    	
    	System.out.println("Enter maximum interval time (in minutes) for sending texts: ");
    	maxInterval = in.nextInt();
    	
    	System.out.println("Enter total time period (in minutes) for which you would like texts to be sent: ");
    	NewThread.totalTimeMinutes = in.nextLong();
    	
    	in.close();
    	
        new NewThread(area,phoneNo,minInterval,maxInterval, NewThread.totalTimeMinutes);
    }
	
}
