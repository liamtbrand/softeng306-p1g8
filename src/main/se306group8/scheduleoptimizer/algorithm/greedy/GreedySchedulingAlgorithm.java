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
	CLIRuntimeMonitor monitor;

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
		this.monitor.start();

		GreedyChildScheduleFinder gcsf = new GreedyChildScheduleFinder(numberOfProcessors);
		TreeSchedule schedule = new TreeSchedule(graph, (TreeSchedule s) -> 0);
		
		while (!schedule.isComplete()) {
			schedule = gcsf.getChildSchedules(schedule).get(0);
		}
		
		// Final (immutable) object to return
		Schedule finalSchedule = schedule.getFullSchedule();
		
		TreeSchedule treeSchedule = (TreeSchedule)finalSchedule;
		
		HashMap<Task, ProcessorAllocation> allocations = new HashMap<Task, ProcessorAllocation>();
		
		for (Task t : treeSchedule.getAllocated()) {
			allocations.put(t, treeSchedule.getAlloctionFor(t));
		}
		
		// Update state of current schedule to CLIRuntimeMonitor
		this.monitor.printGreedySchedule(allocations, finalSchedule.getTotalRuntime());
				
		// Invoke finish() method on RuntimeMonitor instance
		monitor.finish();
		
		// Update RuntimeMonitor instance with finished schedule
		monitor.updateBestSchedule(finalSchedule);
		
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
