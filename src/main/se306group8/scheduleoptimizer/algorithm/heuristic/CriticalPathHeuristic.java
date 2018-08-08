package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.algorithm.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class CriticalPathHeuristic implements MinimumHeuristic {
	@Override
	public int estimate(TreeSchedule schedule) {
		if(schedule.isEmpty()) {
			int max = 0;
			for(Task root : schedule.getGraph().getRoots()) {
				int bottomTime = schedule.getGraph().getBottomTime(root);
				if(bottomTime > max) {
					max = bottomTime;
				}
			}
			
			return max;
		}
		
		//Find the bottom time for every task that has been scheduled
		int heuristic = schedule.getParent().getLowerBound();
		
		//TODO find earliest start time.
		ProcessorAllocation alloc = schedule.getMostRecentAllocation();
			
		int time = schedule.getGraph().getBottomTime(alloc.task) + alloc.startTime;
			
		if(time > heuristic) {
			heuristic = time;
		}
		
		return heuristic;
	}
}
