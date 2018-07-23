package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * The root interface for all algorithms for computing a schedule. The only gurantee is that the schedule will be valid and complete.
 * Subclasses may stipulate that they return optimal solutions.
 */
public interface Algorithm {

	/**
	 * Starts the computation process for computing a valid complete schedule, returning when the computation is complete.
	 * 
	 * @param graph The task graph, must be nonNull
	 * @param numberOfProcessors The maximum number of processors that the algorithm can assign tasks to, must be > 0
	 * @return The complete schedule.
	 */
	Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors);
	
	/**
	 * Sets the monitor that is used to display intermediate results.
	 * 
	 * @param monitor The monitor to use, it this is null the monitor is unset.
	 */
	void setMonitor(RuntimeMonitor monitor);
}
