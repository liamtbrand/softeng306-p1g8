package se306group8.scheduleoptimizer.algorithm;

import java.util.List;
import java.util.ListIterator;

import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Represents an allocation of tasks to different processors in a specific order. 
 * 
 * This is the result object that is returned from the algorithm.
 */
public final class Schedule implements Iterable<List<Task>> {
	private final TaskGraph graph;
	private final List<List<Task>> taskLists;
	
	/**
	 * Creates a schedule.
	 * 
	 * @param graph The task graph that the schedule was generated from.
	 * @param taskLists The list of tasks that is assigned to each processor. Each task must be included in the graph, and each task must
	 *        be used once and only once.
	 */
	Schedule(TaskGraph graph, List<List<Task>> taskLists) {
		assert graph != null && taskLists != null;
		
		this.graph = graph;
		this.taskLists = taskLists;
	}
	
	/** 
	 * Returns the task graph that this schedule was generated from. 
	 */
	public TaskGraph getGraph() {
		return graph;
	}
	
	/** 
	 * Returns the number of processors that were used by the schedule. 
	 */
	public int getNumberOfUsedProcessors() {
		return taskLists.size();
	}
	
	/**
	 * Gets the list of tasks scheduled on a particular processor.
	 * 
	 * @param processor The processor number to query. Must be positive and less than the number of used processors.
	 * @return The list of tasks. This list will not be empty.
	 */
	public List<Task> getTasksOnProcessor(int processor) {
		return taskLists.get(processor);
	}

	@Override
	public ListIterator<List<Task>> iterator() {
		return taskLists.listIterator();
	}
	
	/**
	 * Computes the start time of a particular task.
	 * 
	 * @param task The task to query for. This task must be one of the tasks included in the graph and schedule
	 * @return The start time of the given task.
	 */
	public int getStartTime(Task task) {
		
	}
	
	/**
	 * Queries the processor number that a particular task was assigned to.
	 * 
	 * @param task The task to query. This task must be one of the tasks in the task graph.
	 * @return The processor number.
	 */
	public int getProcessorNumber(Task task) {
		
	}
	
	/** 
	 * Returns the total runtime of this schedule. 
	 */
	public int getTotalRuntime() {
		
	}
}
