package se306group8.scheduleoptimizer.algorithm;

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
	
	public void setProcessors(int numOfProcessors) {
		this.numProcessors = numOfProcessors;
	}
	
	@Override
	public void start() {
		this.startTime = System.nanoTime();
		logMessage("==========================================");
		logMessage("Starting now...");
		logMessage("==========================================");
		
		String processors = "||";
		for (int i = 1; i <= this.numProcessors; i++) {
			processors += "\t" + i + "\t||";
		}
		
		logMessage(processors);

	}

	@Override
	public void finish() {
		this.finishTime = System.nanoTime();
		this.duration = this.finishTime - this.startTime;
		
		logMessage("Finished! Valid schedule found in: " + (double)this.duration/1000000000 + " seconds.");
		logMessage("==========================================");
		
	}

	@Override
	public void updateBestSchedule(Schedule optimalSchedule) {
		// Does nothing on purpose.
		
		//optimalSchedule.
		logMessage("Found new best schedule."); // Just log a message.
	}
	

	@Override
	public void logMessage(String message) {
		// Print messages to stdout.
		System.out.println(message);
	}



}
