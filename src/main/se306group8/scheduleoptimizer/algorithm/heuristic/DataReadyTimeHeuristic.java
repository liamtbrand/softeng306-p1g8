package se306group8.scheduleoptimizer.algorithm.heuristic;

import java.util.Collection;

import se306group8.scheduleoptimizer.algorithm.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class DataReadyTimeHeuristic implements MinimumHeuristic{

	private final int processors;
	
	public DataReadyTimeHeuristic(int processors) {
		this.processors=processors;
	}
	
	@Override
	public int estimate(TreeSchedule schedule) {
		Collection<Task> free = schedule.getAllocatable();
		TaskGraph tg = schedule.getGraph();
		
		//formula from "Reducing the solution space of optimal task scheduling" page 6
		int max = 0;
		
		for (Task n:free) {
			max=Math.max(max, schedule.getDataReadyTime(n));
		}
		
		return max;
		
	}
	
}
