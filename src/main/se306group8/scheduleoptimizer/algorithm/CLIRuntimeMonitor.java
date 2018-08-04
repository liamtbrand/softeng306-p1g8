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
	
	private static int seconds = 0;
	
	public CLIRuntimeMonitor() {
		this.numProcessors = 0;
	}
	
	public CLIRuntimeMonitor(int numProcessors) {
		this.numProcessors = numProcessors;
	}
	
	@Override
	public void start() {
		// Start timing variable
		this.startTime = System.nanoTime();
		logMessage("==========================================");
		logMessage("Welcome! You are running the Greedy Algorithm.\nA valid (greedy) schedule will be returned shortly.");
		logMessage("==========================================");
		
		// Print out number of processors to stdout
		String processors = "t ||";
		String line = "==";
		for (int i = 1; i <= this.numProcessors; i++) {
			processors += "\t" + i + "\t||";
			line += "================";
		}
		
		logMessage(processors);
		logMessage(line);

	}
	
	public void updateCurrentSchedule(HashMap<Task, ListSchedule.ProcessorAllocation> allocations) {
			
			int processor;
			String tasksCurrent = this.seconds + " ||";
			
			// Iterate through all task-allocations and processors, checking which ones are to be logged for a given second
			for (int i = 1; i <= this.numProcessors; i++) {
				for (Map.Entry<Task, ListSchedule.ProcessorAllocation> entry : allocations.entrySet()) {
					if (entry.getValue().processor == i) {
						if ((this.seconds >= entry.getValue().startTime) && (this.seconds <= entry.getValue().endTime)) {
							tasksCurrent += "\t" + entry.getKey().getName() + "\t||";
						}
					}
				}
			}
			
			logMessage(tasksCurrent);
			this.seconds++;
	}

	@Override
	public void finish() {
		
		this.seconds = 0;
		
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
	}
	

	@Override
	public void logMessage(String message) {
		// Print messages to stdout.
		System.out.println(message);
	}



}
