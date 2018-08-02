package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.algorithm.ListSchedule.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class CriticalPathHeuristic implements MinimumHeuristic {
	@Override
	public int estimate(TreeSchedule schedule) {
		//Find the bottom time for every task that has been scheduled
		int heuristic = 0;
		
		//TODO find earliest start time.
		
		for(Task t : schedule.getGraph().getAll()) {
			int time;
			ProcessorAllocation alloc = schedule.getAlloctionFor(t);
			
			//If the task has not been scheduled
			if(alloc == null) {
				time = schedule.getGraph().getBottomTime(t);
			} else {
				time = schedule.getGraph().getBottomTime(t) + alloc.startTime;
			}
			
			if(time > heuristic) {
				heuristic = time;
			}
		}
		
		return heuristic;
	}
}
