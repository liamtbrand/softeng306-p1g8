package se306group8.scheduleoptimizer.taskgraph;

import java.util.List;
import java.util.ListIterator;

/**
 * Represents an allocation of tasks to different processors in a specific order. 
 * 
 * This is the result object that is returned from the algorithm.
 */
public interface Schedule extends Iterable<List<Task>> {
	/** 
	 * Returns the task graph that this schedule was generated from. 
	 */
	public TaskGraph getGraph();
	
	/** 
	 * Returns the number of processors that were used by the schedule. 
	 */
	public int getNumberOfUsedProcessors();
	
	/**
	 * Gets the list of tasks scheduled on a particular processor.
	 * 
	 * If an unused processor is queried an empty list will be returned.
	 */
	public List<Task> getTasksOnProcessor(int processor);

	@Override
	public ListIterator<List<Task>> iterator();
	
	/**
	 * Computes the start time of a particular task.
	 * 
	 * @param task The task to query for. This task must be one of the tasks included in the graph and schedule
	 * @return The start time of the given task.
	 */
	public int getStartTime(Task task);
	
	/**
	 * Queries the processor number that a particular task was assigned to.
	 * 
	 * @param task The task to query. This task must be one of the tasks in the task graph.
	 * @return The processor number.
	 */
	public int getProcessorNumber(Task task);
	
	/** 
	 * Returns the total runtime of this schedule. 
	 */
	public int getTotalRuntime();
}
