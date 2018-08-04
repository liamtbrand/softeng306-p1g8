package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

@FunctionalInterface
public interface MinimumHeuristic {
	/** Provides an lower bound of any partial solution that can be produced from this partial solution. */
	int estimate(TreeSchedule schedule);
}
