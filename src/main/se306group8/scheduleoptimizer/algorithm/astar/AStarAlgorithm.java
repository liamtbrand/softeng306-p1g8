package se306group8.scheduleoptimizer.algorithm.astar;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.storage.ScheduleStorage;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class AStarAlgorithm extends Algorithm {
	private RuntimeMonitor monitor;
	private ChildScheduleFinder childGenerator;
	private MinimumHeuristic heuristic;
	
	public AStarAlgorithm(ChildScheduleFinder childGenerator,MinimumHeuristic heuristic) {
		this.childGenerator=childGenerator;
		this.heuristic=heuristic;
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) {
		if(monitor != null)
			monitor.start();
		
		ScheduleStorage queue = new ScheduleStorage(numberOfProcessors);
		TreeSchedule best = new TreeSchedule(graph, heuristic);
		
		while(!best.isComplete()) {
			queue.storeSchedules(childGenerator.getChildSchedules(best));
			best=queue.getBestSchedule();
			if(monitor != null)
				monitor.setSolutionsExplored(queue.size());
		}
		
		Schedule schedule = best.getFullSchedule();
		
		if(monitor != null)
			monitor.finish(schedule);
		
		return schedule;
	}

}
