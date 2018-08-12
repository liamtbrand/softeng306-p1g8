package se306group8.scheduleoptimizer.algorithm.astar;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.storage.BlockScheduleStorage;
import se306group8.scheduleoptimizer.algorithm.storage.ScheduleStorage;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class AStarSchedulingAlgorithm extends Algorithm {
	
	private final ChildScheduleFinder childGenerator;
	private final MinimumHeuristic heuristic;
	private final ScheduleStorage queue;
	
	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic, RuntimeMonitor monitor, ScheduleStorage storage) {
		super(monitor);
		
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
		this.queue = storage;
	}

	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic) {
		super();
		
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
		this.queue = new BlockScheduleStorage();
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) {
		TreeSchedule best = new TreeSchedule(graph, heuristic, numberOfProcessors);
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		
		TreeSchedule greedySoln = best;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}

		queue.pruneStorage(greedySoln.getRuntime());
		
		while (!best.isComplete()) {

			List<TreeSchedule> children = childGenerator.getChildSchedules(best);

			queue.putAll(children);

			best = queue.pop();
			
			getMonitor().setSolutionsExplored(queue.size());
		}
		
		return best.getFullSchedule();
	}

}
