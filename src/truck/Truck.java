package truck;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Truck implements Runnable{
	private Semaphore entryGateLane;
	private Semaphore stackHandlingSlot;
	private Semaphore exitGateLane;
	private String name;
	private JobType type;
	private JobStatus status;
	
	public Truck(Semaphore entryGateLane, Semaphore stackHandlingSlot, Semaphore exitGateLane, String name, JobType type){
		this.entryGateLane = entryGateLane;
		this.stackHandlingSlot = stackHandlingSlot;
		this.exitGateLane = exitGateLane;
		this.name = name;
		this.type = type;
		this.status = JobStatus.PARKED;
	}
	
	private void entryHandling() {
		try {
			 entryGateLane.acquire();
			_gateHandling(this.entryGateLane, JobStatus.BEING_HANDLED_AT_ENTRY);
			//System.out.println(this.toString() + " entry granted");
		}
		catch(InterruptedException e) {e.printStackTrace();}
		finally {
			entryGateLane.release();
		}
	}
	
	private void transitIn() {
		try {
			transit(" in transit towards stack", " arrived at stack", JobStatus.DRIVING_TO_STACK);
		}catch(InterruptedException e) { e.printStackTrace(); }
	}
	
	private void handleAtStack() {
		try {
			_handleAtStack();
		}catch(InterruptedException e) { e.printStackTrace(); }
		finally {
			stackHandlingSlot.release();
		}
	}
	
	private void transitOut() {
		try {
			transit(" in transit towards exit gate", " arrived at exit gate", JobStatus.DRIVING_TO_EXIT);
		}catch(InterruptedException e) { e.printStackTrace(); }
	}
	
	private void exitHandling() {
		try {
			_gateHandling(this.exitGateLane, JobStatus.BEING_HANDLED_AT_EXIT);
			this.status = JobStatus.FINISHED;
		} catch (InterruptedException e) { e.printStackTrace(); }
		finally {
			exitGateLane.release();
		}
		//System.out.println(name + " left the terminal.");
	}
	
	private void _gateHandling(Semaphore lane, JobStatus status) throws InterruptedException{
		int time;
		if(this.type.equals(JobType.DELIVERY))
			time = ThreadLocalRandom.current().nextInt(2, 5);
		else
			time = ThreadLocalRandom.current().nextInt(3, 9);
		this.status = status;
		//System.out.println( this.toString() + " being processed for " + time + " minutes ");
		Thread.sleep(time * 1000 * 60);

	}
	
	private void _handleAtStack() throws InterruptedException{
		this.status = JobStatus.WAITING_FOR_A_FREE_STACK_SLOT;
		stackHandlingSlot.acquire();
		this.status = JobStatus.BEING_HANDLED_AT_STACK;
		int time = ThreadLocalRandom.current().nextInt(3,9);
		//System.out.println(this.toString() + " handled at stack for " + time  + " minutes");
		Thread.sleep(time * 1000);
		//System.out.println(this.toString()+ " handled.");
	}
	
	private void transit(String travelMsg, String arrivalMsg, JobStatus status) throws InterruptedException{
		//System.out.println(this.toString() + travelMsg);
		this.status = status;
		Thread.sleep(1000 * 3 * 60);
		//System.out.println(this.toString() + arrivalMsg);
		
	}
	
	public String toString() {
		return this.name + "(" + this.type + ")";
	}
	
	public JobStatus getStatus() {
		return this.status;
	}
	
	public JobType getJobType() {
		return this.type;
	}
	
	public void run() {
		entryHandling();
		transitIn();
		handleAtStack();
		transitOut();
		exitHandling();
	}
}
