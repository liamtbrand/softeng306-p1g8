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
	
	// The best COMPLETE schedule found so far
	private TreeSchedule _currentBestSchedule = null;
	private int _currentBest = Integer.MAX_VALUE;
	
	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		TreeSchedule initialSchedule = new TreeSchedule(graph, graph.getAll().get(0), 0, new CriticalPathHeuristic());
		
		TreeSchedule optimalSchedule = branchAndBound(initialSchedule);
		
		return new ListSchedule(graph, optimalSchedule.computeTaskLists());
	}

	private TreeSchedule branchAndBound(TreeSchedule schedule) {
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder();
		
		// Get all children in order from best lower bound to worst
		List<TreeSchedule> childSchedules = greedyFinder.getChildSchedules(schedule);
		
		for (TreeSchedule child : childSchedules) {
			int childLowerBound = child.getLowerBound();
			// Only consider the child if its lower bound is better than current best
			if (childLowerBound < _currentBest) {
				// Check if the child schedule is complete or not
				if (child.getFullSchedule() != null) {
					_currentBestSchedule = child;
					_currentBest = childLowerBound;
				} else {
					branchAndBound(child);
				}
			}
		}
		return _currentBestSchedule;
	}
	
	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		// TODO Auto-generated method stub
	}

}
