package kernel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import truck.JobStatus;
import truck.JobType;
import truck.Truck;


public class Producer implements Runnable{
	
	private BlockingQueue<Truck> queue;
	private Map<Integer, Truck> truckMap;
	
	private Semaphore inboundLane;
	private Semaphore outboundLane;
	private Semaphore stackHandlingSlot;
	
	int truckID = 1;
	
	public Producer(BlockingQueue<Truck> queue, Semaphore inboundLane, Semaphore outboundLane, Semaphore stackHandlingSlot){
		this.queue = queue;
		this.outboundLane = outboundLane;
		this.inboundLane = inboundLane;
		this.stackHandlingSlot = stackHandlingSlot;
		this.truckMap = new HashMap<Integer, Truck>();
	}
	
	public void produce() throws InterruptedException{
		Scanner scanner = new Scanner(System.in);
		while(true) {
			String command = scanner.nextLine();
			
			switch(command.toUpperCase()) {
			
			//COMMAND 1, CREATES DELIVERY TRUCK
			case "CREATE DELIVERY" :
				Truck truck1 = new Truck(this.inboundLane, this.stackHandlingSlot, this.outboundLane, "Truck-" + truckID, JobType.DELIVERY);
				truckMap.put(truckID, truck1);
				queue.put(truck1);
				truckID++;
			break;
			
			//COMMAND 2, CREATES RECEIVAL TRUCK
			case "CREATE RECEIVAL" :
				Truck truck2 = new Truck(this.inboundLane, this.stackHandlingSlot, this.outboundLane, "Truck-" + truckID, JobType.RECEIVAL);
				truckMap.put(truckID, truck2);
				queue.put(truck2);
				truckID++;
			break;
			
			
			//COMMAND 3, PRINTS ALL TRUCKS THAT WAIT AT EXIT AND ENTRY AND THEIR NUMBERS
			case "STATUS GATES" :
				System.out.println("\n================ TRUCKS AT GATES ================");
				if(truckMap.size() > 0) {
					int atEntry = 0, atExit = 0;
					Iterator<Entry<Integer, Truck>> iter = truckMap.entrySet().iterator();
					while(iter.hasNext()){
						Map.Entry<Integer, Truck> entry = iter.next();
						Truck t = entry.getValue();
						if(t.getStatus() == JobStatus.BEING_HANDLED_AT_ENTRY) {
							atEntry++;
							System.out.println(t.toString() + " " + JobStatus.BEING_HANDLED_AT_ENTRY);
						}
						else if(t.getStatus() == JobStatus.BEING_HANDLED_AT_EXIT) {
							atExit++;
							System.out.println(t.toString() + " " + JobStatus.BEING_HANDLED_AT_EXIT);
						}
					}
					System.out.println("\nTrucks handled at entry : " + atEntry);
					System.out.println("Trucks handled at exit : " + atExit);
				}
				else System.out.println("No trucks available");
				System.out.println("============================\n");
			break;
			
			//COMMAND 4, PRINTS ALL TRUCKS THAT ARE HANDLED AT THE STACK
			case "STATUS STACK" :
				System.out.println("\n================ TRUCKS AT STACK ================");
				if(truckMap.size() > 0) {
					int count = 0;
					Iterator<Entry<Integer, Truck>> iter = truckMap.entrySet().iterator();
					while(iter.hasNext()) {
						Map.Entry<Integer, Truck> entry = iter.next();
						Truck t = entry.getValue();
						if(t.getStatus() == JobStatus.BEING_HANDLED_AT_STACK) {
							count++;
							System.out.println(t.toString() + " " + JobStatus.BEING_HANDLED_AT_STACK);
						}
					}
					System.out.println("Trucks waiting at stack : " + count);
				}
				else System.out.println("No trucks available");
				System.out.println("============================\n");
			break;
			
			//COMMAND 5, PRINTS ALL TRUCKS THAT ARE IN TRANSIT
			case "STATUS TRANSIT" :
				System.out.println("\n================ TRUCKS TRANSITING IN TERMINAL ================");
				if(truckMap.size() > 0) {
					int countTransitIn = 0;
					int countTransitOut = 0;
					Iterator<Entry<Integer, Truck>> iter = truckMap.entrySet().iterator();
					while(iter.hasNext()) {
						Map.Entry<Integer, Truck> entry = iter.next();
						Truck t = entry.getValue();
						if(t.getStatus() == JobStatus.DRIVING_TO_STACK) {
							countTransitIn++;
							System.out.println(t.toString() + " " + JobStatus.DRIVING_TO_STACK);
						}
						else if(t.getStatus() == JobStatus.DRIVING_TO_EXIT) {
							countTransitOut++;
							System.out.println(t.toString() + " " + JobStatus.DRIVING_TO_EXIT);
						}
					}
					System.out.println("Trucks driving towards the stack : " + countTransitIn);
					System.out.println("Trucks driving towards the exit : " + countTransitOut);
				}
				else System.out.println("No trucks available");
				System.out.println("============================\n");
			break;
			
			
			//COMMAND 6, SHOWS ALL TRUCKS IN THE TERMINAL AND THEIR JOBS (DELIVERY / RECEIVAL)
			case "STATUS TRUCK JOBS" :
				System.out.println("\n================ TRUCKS IN THE TERMINAL AND THEIR JOBS ================");
				if(truckMap.size() > 0) {
					int countDelivery = 0;
					int countReceival = 0;
					Iterator<Entry<Integer, Truck>> iter = truckMap.entrySet().iterator();
					while(iter.hasNext()) {
						Map.Entry<Integer, Truck> entry = iter.next();
						Truck t = entry.getValue();
						if(t.getJobType() == JobType.DELIVERY) {
							countDelivery++;
						}
						else if(t.getJobType() == JobType.RECEIVAL) {
							countReceival++;
						}
						System.out.println(t.toString());
					}
					System.out.println("\nNumber of trucks receiving containers : " + countReceival);
					System.out.println("Number of trucks delivering containers : " + countDelivery);
				}
				else System.out.println("No trucks available");
				System.out.println("============================\n");
			break;
				
			//COMMAND 7, JOB STATUS OF ALL TRUCKS
			case "STATUS ALL" :
				System.out.println("\n================ STATUS OF ALL TRUCKS ================");
				if(truckMap.size() > 0)
					truckMap.forEach((key, truck) -> System.out.println(truck.toString() + " " + truck.getStatus()));
				else System.out.println("No trucks available");
				System.out.println("============================\n");
				break;
				
			//COMMAND 8, REMOVES ALL TRUCKS THAT ARE IN FINISHED STATE FROM LIST
			case "CLEAR FINISHED" :
				Iterator<Map.Entry<Integer, Truck>> iter = truckMap.entrySet().iterator();
				while(iter.hasNext()) {
					Map.Entry<Integer, Truck> entry = iter.next();
					Truck t = entry.getValue();
					if(t.getStatus() == JobStatus.FINISHED) {
						iter.remove();
					}
				}
				break;
			
			//COMMAND 9, SHOWS LIST OF ALL COMMANDS AND THEIR DESCRIPTIONS
			case "HELP" :
				System.out.println("\n================ COMMANDS ================");
				System.out.println("CREATE DELIVERY : Creates a truck with the job of delivering a container. The truck is initially parked "
						+ "and will wait in a queue for a free inbound lane.");
				System.out.println("CREATE RECEIVAL : Creates a truck with the job of receiving a container. The truck is initially parked "
						+ "and will wait in a queue for a free inbound lane.");
				System.out.println("STATUS GATES : Gives information about the trucks waiting at entry/exit gates.");
				System.out.println("STATUS STACK : Gives information about the trucks handled at the stack.");
				System.out.println("STATUS TRANSIT : Gives information about the trucks traveling to either to the stack or towards the exit gate");
				System.out.println("STATUS TRUCK JOBS : Gives information about the trucks' jobs.");
				System.out.println("STATUS ALL : Gives informations about all trucks such as : Name, Job Type, Location.");
				System.out.println("CLEAR FINISHED : Clears the internal map of trucks that have finished their job.");
				System.out.println("EXIT : Exits the application.");
				System.out.println("============================\n");
			break;
				
			//COMMAND 10, EXITS THE PROGRAM
			case "EXIT" :
				scanner.close();
				System.exit(0);
				break;
				
			default :
				System.out.println("UNKNOWN COMMAND");
				break;
			}
		}
		
	}
	
	public void run() {
		try {
			produce();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
