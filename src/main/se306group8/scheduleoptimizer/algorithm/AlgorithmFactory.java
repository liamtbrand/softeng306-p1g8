package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.algorithm.astar.AStarSchedulingAlgorithm;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.NoIdleTimeHeuristic;
import se306group8.scheduleoptimizer.config.Config;

public class AlgorithmFactory {
	private final int processors;

	public AlgorithmFactory(int processors) {
		this.processors = processors;
	}
	
	public Algorithm getAlgorithm(RuntimeMonitor monitor, Config config) {
		CriticalPathHeuristic critical = new CriticalPathHeuristic();
		NoIdleTimeHeuristic idle = new NoIdleTimeHeuristic(config.P());
		
		MinimumHeuristic heuristic = s -> Math.max(critical.estimate(s), idle.estimate(s));
		
		return new AStarSchedulingAlgorithm(new GreedyChildScheduleFinder(config.P()), heuristic, monitor);
	}
}
