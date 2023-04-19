package truck;

public enum JobStatus {
	PARKED,
	BEING_HANDLED_AT_ENTRY,
	BEING_HANDLED_AT_EXIT,
	BEING_HANDLED_AT_STACK,
	WAITING_FOR_A_FREE_STACK_SLOT,
	DRIVING_TO_STACK,
	DRIVING_TO_EXIT,
	FINISHED
	
}