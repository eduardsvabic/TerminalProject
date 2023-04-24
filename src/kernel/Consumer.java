package kernel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import truck.Truck;

public class Consumer implements Runnable{
	
	private BlockingQueue<Truck> queue;

	ExecutorService service = Executors.newCachedThreadPool();
	
	public Consumer(BlockingQueue<Truck> queue){
		this.queue = queue;
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
