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
	private TreeSchedule dfsBest;
	
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
		getMonitor().logMessage("Starting A*.");
		
		//used for the console output
		boolean contingency = false;

		TreeSchedule best = new TreeSchedule(graph, heuristic, numberOfProcessors);

		queue.signalStorageSizes(getMonitor());
		
		getMonitor().setNumberOfProcessors(numberOfProcessors);
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		
		TreeSchedule greedySoln = best;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}

		queue.put(greedySoln);
		getMonitor().updateBestSchedule(greedySoln);
		
		Runtime memory = Runtime.getRuntime();
		long maxMemory = (long) (memory.maxMemory() * 0.65);
		
		while (!best.isComplete()) {
			
			queue.signalMonitor(getMonitor());
			
			long queuememory = queue.size() * 10L;
			
			if (getMonitor().isInterupted()) {
				throw new InterruptedException();
			}
			
			if ( queuememory < maxMemory) {
				if (contingency) {
					contingency = false;
					getMonitor().logMessage("Switching back to A*");
				}
				explore(best);
			} else {
				
				if (!contingency) {
					contingency = true;
					getMonitor().logMessage("Using contingency plan Branch and Bound");
				}
				//System.out.println("Using contingency plan");
				
				dfsBest = queue.getBestSchedule();
				queue.put(branchAndBound(best));		
				
			}
			
			best = queue.pop();

			// MERGE CONFLICTS - COMMENTED OUT TO CHECK
			//explored += children.size();

			queue.signalMonitor(getMonitor());
			getMonitor().updateBestSchedule(best);
			getMonitor().setSchedulesExplored(explored);
			
			if(Thread.interrupted()) {
				throw new InterruptedException();
			}
		}
		
		return best.getFullSchedule();
	}

	TreeSchedule explore(TreeSchedule best) throws InterruptedException {
		if (getMonitor().isInterupted()) {
			throw new InterruptedException();
		}
		
		List<TreeSchedule> children = childGenerator.getChildSchedules(best);
		
		if(best.isComplete()) {
			queue.put(best);
			return best;
		}

		explored += children.size();
		
		for(TreeSchedule child : children) {
			if(child.getLowerBound() == best.getLowerBound()) {				
				TreeSchedule s = explore(child);
				
				if(s != null) {
					return s;
				}
			} else {
				queue.put(child);
			}
		}
		
		queue.signalMonitor(getMonitor());
		getMonitor().updateBestSchedule(best);
		getMonitor().setSchedulesExplored(explored);
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		return null;
	}
	
	
	private TreeSchedule branchAndBound(TreeSchedule schedule) throws InterruptedException {
		// Get all children in order from best lower bound to worst
		List<TreeSchedule> childSchedules = childGenerator.getChildSchedules(schedule);
		childSchedules.sort(null);
		
		explored += childSchedules.size();
		
		for (TreeSchedule child : childSchedules) {
			// Only consider the child if its lower bound is better than current best
			if (child.getLowerBound() < dfsBest.getRuntime()) {
				if (child.isComplete()) {
					dfsBest=child;
				} else {
					// Check if the child schedule is complete or not
					dfsBest = branchAndBound(child);
				}
			} else {
				break;
			}
		}

		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		getMonitor().setSchedulesExplored(explored);
		
		return dfsBest;
	}
}
