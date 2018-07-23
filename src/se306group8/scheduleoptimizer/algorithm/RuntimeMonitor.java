package se306group8.scheduleoptimizer.algorithm;

/**
 * This component listens to the Algorithm and get updates as the algorithm progresses.
 */
public interface RuntimeMonitor {

	public void updateBestSchedule( Schedule optimalSchedule );
	
	public void logMessage( String message );
	
}
