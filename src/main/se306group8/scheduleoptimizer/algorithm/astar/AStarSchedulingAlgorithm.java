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
	private int explored = 0;
	
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
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) throws InterruptedException {
		TreeSchedule best = new TreeSchedule(graph, heuristic, numberOfProcessors);
		queue.signalStorageSizes(getMonitor());
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		
		TreeSchedule greedySoln = best;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}

		queue.put(greedySoln);
		
		Runtime memory = Runtime.getRuntime();
		
		while (!best.isComplete()) {
			
			
			
			//100 MB
			
			if (memory.freeMemory() > 100_000_000) {
				explore(best);
			} else {
				queue.put(branchAndBound(best));		
				
			}
			
			best = queue.pop();
			
			getMonitor().setSchedulesExplored(explored);
			queue.signalMonitor(getMonitor());

			if(Thread.interrupted()) {
				throw new InterruptedException();
			}
		}
		
		return best.getFullSchedule();
	}

	TreeSchedule explore(TreeSchedule best) {
		List<TreeSchedule> children = childGenerator.getChildSchedules(best);
		
		if(best.isComplete()) {
			queue.put(best);
			return best;
		}
		
		for(TreeSchedule child : children) {
			explored++;
			
			if(child.getLowerBound() < best.getLargestRoot()) {				
				TreeSchedule s = explore(child);
				
				if(s != null) {
					return s;
				}
			} else {
				queue.put(child);
			}
		}
		
		return null;
	}
	
	
	private TreeSchedule branchAndBound(TreeSchedule schedule) throws InterruptedException {
		// Get all children in order from best lower bound to worst
		List<TreeSchedule> childSchedules = childGenerator.getChildSchedules(schedule);
		childSchedules.sort(null);
		TreeSchedule best = queue.getBestSchedule();
		
		explored += childSchedules.size();
		
		for (TreeSchedule child : childSchedules) {
			// Only consider the child if its lower bound is better than current best
			if (best == null || child.getLowerBound() < best.getRuntime()) {
				if (child.isComplete()) {
					best = child;
				} else {
					// Check if the child schedule is complete or not
					best = branchAndBound(child);
				}
			} else {
				break;
			}
		}

		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		getMonitor().setSchedulesExplored(explored);
		
		return best;
	}
}
