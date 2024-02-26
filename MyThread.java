package zsilver_csci201_Assignment2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Semaphore;


import javax.print.event.PrintJobAttributeEvent;

public class MyThread extends Thread {
    private String ticker;
    private int quant;
    private int price;
    private Semaphore semaphore;
    private int balance = 0;
    private int OAbalance;
    private Object balanceLock;
	long startTime;
	private Boolean completed =false;
	private Boolean accounted = false;
	private int wait;
	private SharedBalance sharedAc;

    public MyThread(String ticker, int quant, int price, int wait, SharedBalance sharedAc, Object balanceLock, long startTime) {
        this.ticker = ticker;
        this.quant = quant;
        this.price = price;
        this.sharedAc = sharedAc;
        this.semaphore = SemaphoreMap.getSemaphore(ticker);
        this.wait = wait;
        this.balanceLock = balanceLock;
        this.startTime = startTime;
        

    }
    public int getBalance() {
    	return balance;
    }
//Used the classroom slides for this
    public void run() {
        
        try {
        	Thread.sleep(wait*1000);
            semaphore.acquire();
            if (quant>=0) {
                System.out.println("["+SectoDate()+"] Starting purchase of "+quant+" stocks of " + ticker);
                int cash = Math.abs(quant)*price;
                int value = sharedAc.withdraw(cash);
                if(value==-1) {
                	System.out.println("Transaction failed due to insufficient balance. Unsuccessful purchase of "+quant+ " stocks of "+ticker); 
          

                }
                else {
                	Thread.sleep(2000);
                    System.out.println("["+SectoDate()+"] Finishing purchase of "+quant+" stocks of " + ticker);
    			    System.out.println("Current Balance after trade: " + value);
                }
                }
        
            else {
            	System.out.println("["+SectoDate()+"] Starting sale of "+Math.abs(quant)+" stocks of " + ticker);
                int cash = Math.abs(quant)*price;
                int value = sharedAc.deposit(cash);
                Thread.sleep(3000);
                System.out.println("["+SectoDate()+"] Finishing sale of "+Math.abs(quant)+" stocks of " + ticker);
			    System.out.println("Current Balance after trade: " + value);

            }
            completed=true;

        } catch (InterruptedException ie) {
            System.out.println("MyThread.run IE: " + ie.getMessage());
        } finally {
            semaphore.release();
        }
    }
private String SectoDate() {
	//used piazza discussion code
    long elapsedTime = System.currentTimeMillis() - startTime;
    	 
        DateFormat simple = new SimpleDateFormat(
            "HH:mm:ss:SSS");
        simple.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simple.format(elapsedTime);
}
public boolean isCompleted() {
	return completed;
}
public boolean isAccountedFor() {
	return accounted;
}
public void setAccountedFor() {
	accounted =true;
}
}

