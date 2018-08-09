package se306group8.scheduleoptimizer.algorithm.astar;

import java.util.Collection;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.greedy.GreedySchedulingAlgorithm;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.storage.ScheduleStorage;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class AStarSchedulingAlgorithm extends Algorithm {
	
	private ChildScheduleFinder childGenerator;
	private MinimumHeuristic heuristic;
	
	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic, RuntimeMonitor monitor) {
		super(monitor);
		
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
	}

	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic) {
		super();
		
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) {
		
		int storageSizeLimit = 1000000; // TODO calculate properly.
		
		ScheduleStorage queue = new ScheduleStorage(storageSizeLimit);
		TreeSchedule best = new TreeSchedule(graph, heuristic);
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		
		TreeSchedule greedySoln = best;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}
		
		queue.storeSchedule(greedySoln);
		int upperBound = greedySoln.getRuntime();
		
		while(!best.isComplete()) {
			
			List<TreeSchedule> children = childGenerator.getChildSchedules(best);
			
			//if one child is complete they are all complete
			if (children.get(0).isComplete()) {
				
				//sort by lowest runtime
				children.sort((TreeSchedule s1, TreeSchedule s2) -> s1.getRuntime() - s2.getRuntime());
				TreeSchedule completeSchedule = children.get(0);
				
				//if false all children are useless
				if (completeSchedule.getRuntime() < upperBound) {
					queue.storeSchedule(completeSchedule);
					upperBound  = completeSchedule.getRuntime();
				}
				
			}else {
				for (TreeSchedule partial:children ) {
					if (partial.getLowerBound() < upperBound) {
						queue.storeSchedule(partial);
					}
				}
			}
			
			
			best = queue.getBestSchedule();
			getMonitor().setSolutionsExplored(queue.size());
		}
		
		return best.getFullSchedule();
	}

}
