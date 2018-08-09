package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Uses RuntimeMonitor to monitor the status of the algorithm.
 * Algorithms should implement the hook method.
 * Algorithms should call updateBestSchedule and logMessage on themselves to update the monitor.
 */
public abstract class MonitoredAlgorithm implements Algorithm {
	
	private RuntimeMonitor runtimeMonitor = new StubRuntimeMonitor();
	
	public abstract Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors);

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
	
	public void setMonitor(RuntimeMonitor monitor) {
		if(monitor != null) {
			runtimeMonitor = monitor;
		}
	}

	/**
	 * Update the RuntimeMonitor with the latest TreeSchedule
	 * @see #runtimeMonitor
	 * @param optimalSchedule
	 */
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		runtimeMonitor.updateBestSchedule(optimalSchedule);
	}

	/**
	 * Update the RuntimeMonitor with a message.
	 * @see #runtimeMonitor
	 * @param message
	 */
	public void logMessage(String message) {
		runtimeMonitor.logMessage(message);
	}
}
