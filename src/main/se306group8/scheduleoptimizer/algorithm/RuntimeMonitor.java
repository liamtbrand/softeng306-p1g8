package se306group8.scheduleoptimizer.algorithm;

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
	public void start(String name, String graphName, int numberOfProcessors, int coresToUseForExecution);
	
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

	/** 
	 * Sets the number of solutions found so far.
	 **/
	public void setSchedulesExplored(long number);

	public void setSchedulesInArray(long number);
	public void setScheduleInArrayStorageSize(int bytes);

	public void setSchedulesInQueue(long number);
	public void setScheduleInQueueStorageSize(int bytes);

	public void setSchedulesOnDisk(long number);
	public void setScheduleOnDiskStorageSize(int bytes);

	public default void interuptAlgorithm() {throw new UnsupportedOperationException();};
	public default boolean isInterupted() {return false;};
	
	default void setScheduleDistribution(int[] distribution, int limit) {  }
	default void setBucketSize(int granularity) {  }

	default void setUpperBound(int bound) {  }
	default void setLowerBound(int lowerBound) {  }
}
