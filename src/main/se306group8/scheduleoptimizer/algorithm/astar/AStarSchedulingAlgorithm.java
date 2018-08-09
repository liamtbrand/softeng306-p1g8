package se306group8.scheduleoptimizer.algorithm.astar;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.storage.ScheduleStorage;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class AStarSchedulingAlgorithm extends Algorithm {
	
	private ChildScheduleFinder childGenerator;
	private MinimumHeuristic heuristic;
	
	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic) {
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) {
		
		int storageSizeLimit = 1000000; // TODO calculate properly.
		
		ScheduleStorage queue = new ScheduleStorage(storageSizeLimit);
		TreeSchedule best = new TreeSchedule(graph, heuristic);
		
		while(!best.isComplete()) {
			queue.storeSchedules(childGenerator.getChildSchedules(best));
			best = queue.getBestSchedule();
			getMonitor().setSolutionsExplored(queue.size());
		}
		
		return best.getFullSchedule();
	}

}
