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
			max=Math.max(max, tdr(n,schedule)+tg.getBottomTime(n));
		}
		
		return max;
		
	}
	
	//formula from "Reducing the solution space of optimal task scheduling" page 3
	private int tdr(Task nj, int p, TreeSchedule schedule) {
		int max = 0;
		for (Dependency dep:nj.getParents()) {
			ProcessorAllocation pa = schedule.getAlloctionFor(dep.getSource());
			if (pa.processor==p) {
				max = Math.max(max, pa.endTime);
			}else{
				max = Math.max(max, pa.endTime + dep.getCommunicationCost());
			}
		}
		return max;
	}
	
	//formula from "Reducing the solution space of optimal task scheduling" page 5
	private int tdr(Task n, TreeSchedule schedule) {
		int min = Integer.MAX_VALUE;
		for (int p=1;p<=processors;p++) {
			min=Math.min(min, tdr(n,p,schedule));
		}
		return min;
	}

}
