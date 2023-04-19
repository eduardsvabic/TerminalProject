package app;

import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import kernel.Consumer;
import kernel.Producer;
import truck.Truck;

public class Mainer{
	
	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<Truck> queue = new ArrayBlockingQueue<Truck>(15);
		Map<Integer, Truck> truckMap = Collections.synchronizedMap(new HashMap<Integer, Truck>());
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("\n================ APP INIT ================");
		System.out.print("Please enter the number of INBOUND lanes : ");
		int inboundLanes = scanner.nextInt();
		Semaphore entryGateLanes = new Semaphore(inboundLanes);
		System.out.print("\nPlease enter the number of OUTBOUND lanes : ");
		int outboundLanes = scanner.nextInt();
		Semaphore exitGateLanes = new Semaphore(outboundLanes);
		
		
		System.out.print("\nPlease enter the number of STACK handling spots: ");
		int slots = scanner.nextInt();
		Semaphore stackHandlingSlots = new Semaphore(slots);
		
		System.out.println("\n================ APP START ================");
		System.out.println("TYPE 'HELP' AND HIT ENTER TO SEE THE LIST OF AVAILABLE COMMANDS");
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.submit(new Producer(queue, entryGateLanes, exitGateLanes, stackHandlingSlots));
		executor.submit(new Consumer(queue, entryGateLanes, exitGateLanes, stackHandlingSlots));
		
	}
}