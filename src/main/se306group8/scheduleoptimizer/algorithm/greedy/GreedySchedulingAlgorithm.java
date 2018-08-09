package se306group8.scheduleoptimizer.algorithm.greedy;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Non optimal greedy algorithm for task scheduling
 */
public class GreedySchedulingAlgorithm extends Algorithm {

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) {

		GreedyChildScheduleFinder gcsf = new GreedyChildScheduleFinder(numberOfProcessors);
		TreeSchedule schedule = new TreeSchedule(graph, (TreeSchedule s) -> 0);
		
		while (!schedule.isComplete()) {
			schedule = gcsf.getChildSchedules(schedule).get(0);
		}
		
		// Final (immutable) object to return
		return schedule.getFullSchedule();
	}

}
