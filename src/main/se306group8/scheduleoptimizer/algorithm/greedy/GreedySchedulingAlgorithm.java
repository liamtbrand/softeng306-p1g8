package se306group8.scheduleoptimizer.algorithm.greedy;

import java.util.HashMap;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.CLIRuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.algorithm.ListSchedule.ProcessorAllocation;

/**
 * Non optimal greedy algorithm for task scheduling
 */
public class GreedySchedulingAlgorithm implements Algorithm {

	// TODO use the monitor
	private RuntimeMonitor monitor;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		// Create new RuntimeMonitor instance
		if (this.monitor == null) {
			this.monitor = new CLIRuntimeMonitor(numberOfProcessors);
		}
		
		// Invoke start() method on RuntimeMonitor instance
		if(monitor != null) {
			this.monitor.start();
		}

		GreedyChildScheduleFinder gcsf = new GreedyChildScheduleFinder(numberOfProcessors);
		TreeSchedule schedule = new TreeSchedule(graph, (TreeSchedule s) -> 0);
		
		while (!schedule.isComplete()) {
			schedule = gcsf.getChildSchedules(schedule).get(0);
		}
		
		// Final (immutable) object to return
		Schedule finalSchedule = schedule.getFullSchedule();
		
		if(monitor != null) {
			monitor.finish(finalSchedule);
		}
		
		return finalSchedule;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		this.monitor = (CLIRuntimeMonitor)monitor;
	}
}
