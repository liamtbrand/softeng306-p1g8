package se306group8.scheduleoptimizer.algorithm.greedy;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Non optimal greedy algorithm for task scheduling
 */
public class GreedySchedulingAlgorithm implements Algorithm {

	// TODO use the monitor
	RuntimeMonitor monitor;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {

		GreedyChildScheduleFinder gcsf = new GreedyChildScheduleFinder(numberOfProcessors);
		TreeSchedule schedule = new TreeSchedule(graph, (TreeSchedule s) -> 0);
		
		while (!schedule.isComplete()) {
			schedule = gcsf.getChildSchedules(schedule).get(0);
		}
		
		return schedule.getFullSchedule();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		this.monitor = monitor;

	}
}
