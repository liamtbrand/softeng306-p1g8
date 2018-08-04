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
	
	public void updateCurrentSchedule(HashMap<Task, ListSchedule.ProcessorAllocation> allocations, int maxFinishTime) {
			
			// input maxFinishTime presents upper bound on number of times to loop (and print)
			for(int secs = 0; secs <= maxFinishTime; secs++) {
			
				String tasksCurrent = secs + " ||";


				
				
				// for every processor
				for (int i = 1; i <= this.numProcessors; i++) {
					boolean isPrinted = false;
					
					for (Map.Entry<Task, ListSchedule.ProcessorAllocation> entry : allocations.entrySet()) {
						
						ListSchedule.ProcessorAllocation processorAllo = entry.getValue();
						Task currentTask = entry.getKey();
						
						if (processorAllo.processor == i) {
							if ((secs >= processorAllo.startTime) && (secs < processorAllo.endTime)) {
								tasksCurrent += "\t" + currentTask.getName() + "\t||";
								info += "\nTask name: " + currentTask.getName() + ", start-time: " 
								+ processorAllo.startTime + ", end-time: " + processorAllo.endTime;
								isPrinted = true;
							}
						}
					}
					if (!isPrinted) {
						tasksCurrent += "\t \t||";
					}
					
				}
				logMessage(tasksCurrent);
			}
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
