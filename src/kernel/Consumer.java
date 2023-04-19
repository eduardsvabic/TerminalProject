package kernel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import truck.Truck;

public class Consumer implements Runnable{
	
	BlockingQueue<Truck> queue;

	Semaphore entryGateLane;
	Semaphore exitGateLane;
	Semaphore stackHandlingSlot;
	
	ExecutorService service = Executors.newCachedThreadPool();
	
	public Consumer(BlockingQueue<Truck> queue, Semaphore entryGateLane, Semaphore exitGateLane, Semaphore stackHandlingSlot){
		this.queue = queue;
		this.exitGateLane = exitGateLane;
		this.entryGateLane = entryGateLane;
		this.stackHandlingSlot = stackHandlingSlot;
	}
	
	public void consume() throws InterruptedException{
		while(true) {
			service.submit(queue.take());
		}
	}
	
	public void run() {
		try {
			consume();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
