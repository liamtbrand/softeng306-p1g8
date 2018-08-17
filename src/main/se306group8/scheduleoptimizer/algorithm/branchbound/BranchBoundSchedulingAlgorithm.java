package se306group8.scheduleoptimizer.algorithm.branchbound;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class BranchBoundSchedulingAlgorithm extends Algorithm {
  
	private final ChildScheduleFinder finder;
	private final MinimumHeuristic heuristic;
	
	private int visited = 0;
	
	public BranchBoundSchedulingAlgorithm(ChildScheduleFinder finder, MinimumHeuristic heuristic, RuntimeMonitor monitor) {
		super(monitor);
		
		this.finder = finder;
		this.heuristic = heuristic;
	}

	public BranchBoundSchedulingAlgorithm(ChildScheduleFinder finder, MinimumHeuristic heuristic) {
		super();
		
		this.finder = finder;
		this.heuristic = heuristic;
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) throws InterruptedException {

		visited = 1;
		TreeSchedule emptySchedule = new TreeSchedule(graph, heuristic, numberOfProcessors);
		
		// Kick off BnB (current 'best schedule' is null)
		Schedule schedule =  branchAndBound(emptySchedule, null, numberOfProcessors).getFullSchedule();
		
		return schedule;
	}

	@Override
	public String toString() {
		return "DFS Branch & Bound";
	}

	private TreeSchedule branchAndBound(TreeSchedule schedule, TreeSchedule best, int numberOfProcessors) throws InterruptedException {
		// Get all children in order from best lower bound to worst
		// TODO add processor number to GCSF
		List<TreeSchedule> childSchedules = finder.getChildSchedules(schedule);
		childSchedules.sort(null);
		
		visited += childSchedules.size();
		
		for (TreeSchedule child : childSchedules) {
			// Only consider the child if its lower bound is better than current best
			if (best == null || child.getLowerBound() < best.getRuntime()) {
				if (child.isComplete()) {
					best = child;
				} else {
					// Check if the child schedule is complete or not
					best = branchAndBound(child, best, numberOfProcessors);
				}
			} else {
				break;
			}
		}

		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		getMonitor().updateBestSchedule(best);
		getMonitor().setSchedulesExplored(visited);
		
		return best;
	}

}
