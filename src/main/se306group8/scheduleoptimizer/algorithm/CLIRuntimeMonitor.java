package se306group8.scheduleoptimizer.algorithm;

import java.math.BigDecimal;

import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class CLIRuntimeMonitor implements RuntimeMonitor {
	
	// Variables to store time values
	long duration;
	long startTime;
	long finishTime;
	
	int numProcessors;
	
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
		String processors = "||";
		String line = "==";
		for (int i = 1; i <= this.numProcessors; i++) {
			processors += "\t" + i + "\t||";
			line += "================";
		}
		
		logMessage(processors);
		logMessage(line);

	}
	
	//public void updateSchedule()

	@Override
	public void finish() {
		// Log and print out finished time
		this.finishTime = System.nanoTime();
		this.duration = this.finishTime - this.startTime;
		
		logMessage("Finished! Valid schedule found in: " + new BigDecimal("" + this.duration/1000000000.0) + " seconds.");
		logMessage("==========================================");
		logMessage("Statistics: ");
		
	}

	@Override
	public void updateBestSchedule(Schedule optimalSchedule) {
		// Print statistics about final schedule
		logMessage("Number of used processors: " + optimalSchedule.getNumberOfUsedProcessors() + "/" + this.numProcessors);
		logMessage("Total runtime of output schedule: " + optimalSchedule.getTotalRuntime());
		
		
	}
	

	@Override
	public void logMessage(String message) {
		// Print messages to stdout.
		System.out.println(message);
	}



}
