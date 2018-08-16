package se306group8.scheduleoptimizer.algorithm.greedy;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Non optimal greedy algorithm for task scheduling
 */
public class GreedySchedulingAlgorithm extends Algorithm {

	public GreedySchedulingAlgorithm(RuntimeMonitor monitor) {
		super(monitor);
	}

	public GreedySchedulingAlgorithm() {
		super();
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) throws InterruptedException {

		getMonitor().logMessage("Starting Greedy.");

		GreedyChildScheduleFinder gcsf = new GreedyChildScheduleFinder(numberOfProcessors);
		TreeSchedule schedule = new TreeSchedule(graph, (TreeSchedule s) -> 0, numberOfProcessors);
		
		while (!schedule.isComplete()) {
			getMonitor().updateBestSchedule(schedule);
			schedule = gcsf.getChildSchedules(schedule).get(0);

			if(Thread.interrupted()) {
				throw new InterruptedException();
			}
		}
		
		// Final (immutable) object to return
		return schedule.getFullSchedule();
	}

}
