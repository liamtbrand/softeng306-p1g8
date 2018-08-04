package se306group8.scheduleoptimizer.algorithm;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import se306group8.scheduleoptimizer.algorithm.ListSchedule.ProcessorAllocation;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class CLIRuntimeMonitor implements RuntimeMonitor {
	
	// Variables to store time values
	long duration;
	long startTime;
	long finishTime;
	
	int numProcessors;
	
	String info = "";
	
	public CLIRuntimeMonitor() {
		this.numProcessors = 0;
	}
	
	// Constructor 
	public CLIRuntimeMonitor(int numProcessors) {
		this.numProcessors = numProcessors;
	}
	
	@Override
	public void start() {
		// Start timing variable
		this.startTime = System.nanoTime();
		logMessage("==========================================");
		logMessage("TEAM: 'Team Name is Trivial and Left as a Exercise for the Reader'");
		logMessage("Welcome! You are running the Greedy Algorithm.\nA valid (greedy) schedule will be returned shortly.");
		logMessage("==========================================");
		
		// Print out number of processors to stdout (the top row)
		String processors = "t ||";
		String line = "==";
		for (int i = 1; i <= this.numProcessors; i++) {
			processors += "\t" + i + "\t||";
			line += "================";
		}
		
		logMessage(processors);
		logMessage(line);

	}
	
	
	// Method that prints out final schedule to stdout
	public void printGreedySchedule(HashMap<Task, ListSchedule.ProcessorAllocation> allocations, int maxFinishTime) {
			
			// input maxFinishTime presents upper bound on number of times to loop (and print)
			for(int secs = 0; secs < maxFinishTime; secs++) {
			
				// Starting column (for time)
				String tasksCurrent = (secs + 1) + " ||";
				
				// Loop through processors
				for (int currentProcNum = 1; currentProcNum <= this.numProcessors; currentProcNum++) {
					
					// Variable to keep track of whether or not to print an empty slot (processor doesn't have 
					boolean isPrinted = false;
					
					// Loop through all processorAllocation values, in the input HashMap of the greedy schedule
					for (Map.Entry<Task, ListSchedule.ProcessorAllocation> entry : allocations.entrySet()) {
						
						ProcessorAllocation currentProcessorAllocation = entry.getValue();
						Task currentTask = entry.getKey();
						
						// Validate that the given task/processorAllocation key-value pair is valid to print out at this time
						if (currentProcessorAllocation.processor == currentProcNum) {
							if ((secs >= currentProcessorAllocation.startTime) && (secs < currentProcessorAllocation.endTime)) {
								tasksCurrent += formatTaskName(currentTask.getName());
								isPrinted = true;
							}
						}
					}
					
					// If the given task is not valid, and thus has been passed over, print empty time slot
					if (!isPrinted) {
						tasksCurrent += formatTaskName(" ");
					}
				}
				// Send composed String line to stdout
				logMessage(tasksCurrent);
			}
	}

	/**
	 * Helper method to correctly format a task in the output.
	 * @param taskName Name of task to be printed 
	 * @return concatenated task name in correct format
	 */
	private String formatTaskName(String taskName) {
		return "\t" + taskName + "\t||";
	}
	
	
	@Override
	public void finish() {
	
		
		// Log and print out finished time
		this.finishTime = System.nanoTime();
		this.duration = this.finishTime - this.startTime;
		
		logMessage("Finished! Valid schedule found in: " + new BigDecimal("" + this.duration/1000000000.0) + " seconds.");
		logMessage("==========================================");
		logMessage("Statistics: ");
		
	}

	// Note for greedy implementation, the optimalSchedule is only needed to be 'updated' once, at the end
	@Override
	public void updateBestSchedule(Schedule optimalSchedule) {
		// Print statistics about final schedule
		logMessage("Number of used processors: " + optimalSchedule.getNumberOfUsedProcessors() + "/" + this.numProcessors);
		logMessage("Total runtime of output schedule: " + optimalSchedule.getTotalRuntime());
		logMessage("==========================================");
		logMessage("\n\n");
		
		// Debug purposes
		logMessage(info);
		info = "";
	}
	
	@Override
	public void logMessage(String message) {
		// Print messages to stdout.
		System.out.println(message);
	}



}
