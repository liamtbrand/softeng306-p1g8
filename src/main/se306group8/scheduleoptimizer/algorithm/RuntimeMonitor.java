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
	public void start(String name, int numberOfProcessors, int coresToUseForExecution);
	
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
	public void setSchedulesExplored(int number);

	public void setSchedulesInArray(int number);
	public void setScheduleInArrayStorageSize(int bytes);

	public void setSchedulesInQueue(int number);
	public void setScheduleInQueueStorageSize(int bytes);

	public void setSchedulesOnDisk(int number);
	public void setScheduleOnDiskStorageSize(int bytes);
	
	public void setNumberOfProcessors(int processors);

	default void setScheduleDistribution(int[] distribution, int limit) {  }
	default void setBucketSize(int granularity) {  }

}
