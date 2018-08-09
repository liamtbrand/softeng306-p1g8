package se306group8.scheduleoptimizer.algorithm.branchbound;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.MonitoredAlgorithm;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class BranchBoundSchedulingAlgorithm extends MonitoredAlgorithm {
	
	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) {
		
		TreeSchedule emptySchedule = new TreeSchedule(graph, new CriticalPathHeuristic());
		
		// Kick off BnB (current 'best schedule' is null)
		return branchAndBound(emptySchedule, null, numberOfProcessors);
	}

	private Schedule branchAndBound(TreeSchedule schedule, Schedule best, int numberOfProcessors) {
		// Get all children in order from best lower bound to worst
		// TODO add processor number to GCSF
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		List<TreeSchedule> childSchedules = greedyFinder.getChildSchedules(schedule);
		
		for (TreeSchedule child : childSchedules) {
			
			// Only consider the child if its lower bound is better than current best
			if (best == null || child.getLowerBound() < best.getTotalRuntime()) {
				if (child.isComplete()) {
					best = child.getFullSchedule();
				} else {
					// Check if the child schedule is complete or not
					best = branchAndBound(child, best, numberOfProcessors);
				}
			}
			
		}	
		return best;
	}

}
