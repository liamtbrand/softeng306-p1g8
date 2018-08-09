package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * The root class for all algorithms for computing a schedule.
 * The only guarantee is that the schedule will be valid and complete.
 * Subclasses may stipulate that they return optimal solutions.
 * 
 * Uses RuntimeMonitor to monitor the status of the algorithm.
 * Algorithms should implement the hook method.
 * Algorithms should use getMonitor() to talk with the monitor.
 */
public abstract class Algorithm {
	
	private RuntimeMonitor runtimeMonitor;
	
	public Algorithm(RuntimeMonitor monitor) {
		runtimeMonitor = monitor;
	}
	
	public Algorithm() {
		this(new StubRuntimeMonitor());
	}
	
	public abstract Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors);

	/**
	 * Starts the computation process for computing a valid complete schedule, returning when the computation is complete.
	 * 
	 * @param graph The task graph, must be nonNull
	 * @param numberOfProcessors The maximum number of processors that the algorithm can assign tasks to, must be positive.
	 * @return The complete schedule.
	 */
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		if(runtimeMonitor != null) {
			runtimeMonitor.start();
		}
		
		Schedule solution = produceCompleteScheduleHook(graph, numberOfProcessors);
		
		if(runtimeMonitor != null) {
			runtimeMonitor.finish(solution);
		}
		
		return solution;
	}
	
	/**
	 * Sets the monitor that is used to display intermediate results.
	 * 
	 * @param monitor The monitor to use, if this is null the monitor is un-set.
	 */
	public void setMonitor(RuntimeMonitor monitor) {
		if(monitor != null) {
			runtimeMonitor = monitor;
		}
	}
	
	/**
	 * For use by an algorithm implementing the hook method to get the current monitor.
	 * @return
	 */
	protected RuntimeMonitor getMonitor() {
		return runtimeMonitor;
	}
}
