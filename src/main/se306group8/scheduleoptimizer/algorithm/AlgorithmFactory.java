package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.algorithm.astar.AStarSchedulingAlgorithm;
import se306group8.scheduleoptimizer.algorithm.branchbound.BranchBoundSchedulingAlgorithm;
import se306group8.scheduleoptimizer.algorithm.branchbound.ParallelBranchBoundSchedulingAlgorithm;
import se306group8.scheduleoptimizer.algorithm.childfinder.DuplicateRemovingChildFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.DataReadyTimeHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.NoIdleTimeHeuristic;
import se306group8.scheduleoptimizer.algorithm.storage.BlockScheduleStorage;
import se306group8.scheduleoptimizer.config.Config;

public class AlgorithmFactory {
	
	public static Algorithm getAlgorithm(RuntimeMonitor monitor, Config config) {

		MinimumHeuristic heuristic = new MinimumHeuristic() {

			private final CriticalPathHeuristic criticalPathHeuristic;
			private final NoIdleTimeHeuristic noIdleTimeHeuristic;
			private final DataReadyTimeHeuristic dataReadyTimeHeuristic;

			{
				criticalPathHeuristic = new CriticalPathHeuristic();
				noIdleTimeHeuristic = new NoIdleTimeHeuristic(config.processorsToScheduleOn());
				dataReadyTimeHeuristic = new DataReadyTimeHeuristic(config.processorsToScheduleOn());
			}

			@Override
			public int estimate(TreeSchedule schedule) {
				int criticalPathHeuristicEstimate = criticalPathHeuristic.estimate(schedule);
				int noIdleTimeHeuristicEstimate = noIdleTimeHeuristic.estimate(schedule);
				int dataReadyTimeHeuristicEstimate = dataReadyTimeHeuristic.estimate(schedule);
				return Math.max(
						criticalPathHeuristicEstimate,
						Math.max(
								noIdleTimeHeuristicEstimate,
								dataReadyTimeHeuristicEstimate
						)
				);
			}
		};

		// If not parallelised:
		if (config.coresToUseForExecution() == 1) {
			if(config.visualize()) {
				// If not parallel and visualised, use
				return new AStarSchedulingAlgorithm(new DuplicateRemovingChildFinder(config.processorsToScheduleOn()), heuristic, monitor, new BlockScheduleStorage());
			} else {
				return new BranchBoundSchedulingAlgorithm(new DuplicateRemovingChildFinder(config.processorsToScheduleOn()), heuristic, monitor);
			}
		}else {
			// If parallelised, use parallel branch and bound.
			return new ParallelBranchBoundSchedulingAlgorithm(new DuplicateRemovingChildFinder(config.processorsToScheduleOn()), heuristic, monitor, config.coresToUseForExecution());
		}
		
	}

}
