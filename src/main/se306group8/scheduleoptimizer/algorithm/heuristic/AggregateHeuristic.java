package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

public class AggregateHeuristic implements MinimumHeuristic{
	
	MinimumHeuristic[] heuristics;
	
	public AggregateHeuristic(MinimumHeuristic ...heuristics) {
		this.heuristics=heuristics;
	}

	@Override
	public int estimate(TreeSchedule schedule) {
		int max=0;
		for (MinimumHeuristic h:heuristics) {
			max=Math.max(max, h.estimate(schedule));
		}
		return max;
	}
	
}
