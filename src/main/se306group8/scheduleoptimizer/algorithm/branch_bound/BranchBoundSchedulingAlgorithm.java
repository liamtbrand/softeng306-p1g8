package se306group8.scheduleoptimizer.algorithm.branch_bound;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.algorithm.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.ListSchedule;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class BranchBoundSchedulingAlgorithm implements Algorithm {
	
	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder();
		TreeSchedule initialSchedule = new TreeSchedule(graph, graph.getAll().get(0), 0, new CriticalPathHeuristic());
		
		List<TreeSchedule> activeSchedules = new ArrayList<TreeSchedule>();
		activeSchedules.add(initialSchedule);
		int best = Integer.MAX_VALUE;
		TreeSchedule currentBest = null;
		
		while (!activeSchedules.isEmpty()) {
			TreeSchedule selectedSchedule = activeSchedules.get(0);
			activeSchedules.remove(selectedSchedule);
			List<TreeSchedule> childSchedules = greedyFinder.getChildSchedules(selectedSchedule);
			for (TreeSchedule child : childSchedules) {
				// check if estimate better than best
				if (child.getLowerBound() < best) {
					// check if child is a full schedule
					if (child.getFullSchedule() != null) {
					best = child.getLowerBound();
					currentBest = child;
					} else {
					activeSchedules.add(child);
					}
				}
			}
		}
		
		return new ListSchedule(graph, currentBest.computeTaskLists());
	}

	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		// TODO Auto-generated method stub
	}

}
