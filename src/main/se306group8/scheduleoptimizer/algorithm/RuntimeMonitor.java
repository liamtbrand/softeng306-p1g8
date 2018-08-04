package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.taskgraph.Schedule;

/**
 * This component listens to the Algorithm and get updates as the algorithm progresses.
 */
public interface RuntimeMonitor {

	// To be used to update RuntimeMonitor on current best comlpete schedule
	public void updateBestSchedule( Schedule optimalSchedule );
	
	// Method to be called upon algorithm-start
	public void start();
	
	// Method to be called upon algorithm-finish
	public void finish();
	
	// Passes messages to stdout
	public void logMessage( String message );
	
}
