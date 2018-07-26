package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.taskgraph.Schedule;

/**
 * This component listens to the Algorithm and get updates as the algorithm progresses.
 */
public interface RuntimeMonitor {

	public void updateBestSchedule( Schedule optimalSchedule );
	
	public void logMessage( String message );
	
}
