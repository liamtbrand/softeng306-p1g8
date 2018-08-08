package se306group8.scheduleoptimizer.algorithm.greedy;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.visualisation.RuntimeMonitor;

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
		this.monitor = monitor;
	}
}
