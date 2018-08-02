package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class CLIRuntimeMonitor implements RuntimeMonitor {

	@Override
	public void updateBestSchedule(Schedule optimalSchedule) {
		// Does nothing on purpose.
		logMessage("Found new best schedule."); // Just log a message.
	}

	@Override
	public void logMessage(String message) {
		// Print messages to stdout.
		System.out.println(message);
	}

}
