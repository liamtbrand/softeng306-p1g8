package se306group8.scheduleoptimizer;

/**
 * 
 * Represents an allocation of tasks to different processors.
 *
 */
public interface Schedule {

	public int getNumberOfProcessors();
	
	public int getTasksOnProcessor( int processor );
	
}
