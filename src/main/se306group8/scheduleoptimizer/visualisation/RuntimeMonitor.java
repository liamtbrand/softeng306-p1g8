package se306group8.scheduleoptimizer.visualisation;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;

/**
 * This component listens to the Algorithm and get updates as the algorithm progresses.
 */
public interface RuntimeMonitor {

	/**
	 * To be used to update RuntimeMonitor on current best schedule (this may be partial)
	 * @param optimalSchedule
	 */
	public void updateBestSchedule( TreeSchedule optimalSchedule );
	
	/**
	 * Method to be called upon algorithm-start
	 */
	public void start();
	
	/**
	 * Method to be called upon algorithm-finish
	 * @param solution
	 */
	public void finish(Schedule solution);
	
	/**
	 * Passes messages to stdout
	 * @param message
	 */
	public void logMessage( String message );
	
}
