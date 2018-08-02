package se306group8.scheduleoptimizer.algorithm.branch_bound;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.algorithm.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.ListSchedule;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class BranchBoundSchedulingAlgorithm implements Algorithm {
	
	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		TreeSchedule initialSchedule = new TreeSchedule(graph, graph.getAll().get(0), 0, new CriticalPathHeuristic());
		
		return branchAndBound(initialSchedule, null);
	}

	private Schedule branchAndBound(TreeSchedule schedule, Schedule best) {
	
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder();
		
		// Get all children in order from best lower bound to worst
		List<TreeSchedule> childSchedules = greedyFinder.getChildSchedules(schedule);
		
		for (TreeSchedule child : childSchedules) {
			if (best == null && child.getFullSchedule() != null) {
				best = new ListSchedule(schedule.getGraph(), child.computeTaskLists());
			}
			// Only consider the child if its lower bound is better than current best
			if (child.getLowerBound() < best.getTotalRuntime()) {
				if (child.getFullSchedule() != null) {
					best = new ListSchedule(schedule.getGraph(), child.computeTaskLists());
				} else {
					// Check if the child schedule is complete or not
					best = branchAndBound(child, best);
				}
			} 
		}
		
		return best;
	}
	
	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		// TODO Auto-generated method stub
	}

}
