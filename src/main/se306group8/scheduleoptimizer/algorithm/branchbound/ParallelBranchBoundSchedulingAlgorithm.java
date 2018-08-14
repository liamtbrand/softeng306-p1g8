package se306group8.scheduleoptimizer.algorithm.branchbound;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicReference;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class ParallelBranchBoundSchedulingAlgorithm extends Algorithm{
	
	private ForkJoinPool pool;
	private final int parallelism;
	private AtomicReference<TreeSchedule> bestSoFar;
	
	private final ChildScheduleFinder finder;
	private final MinimumHeuristic heuristic;
	
	private class ForkJob extends RecursiveAction {
		private TreeSchedule jobSchedule;
		
		public ForkJob(TreeSchedule job) {
			jobSchedule=job;
		}

		@Override
		protected void compute() {
			compute(jobSchedule);
		}
		
		private void compute(TreeSchedule tree) {
			List<TreeSchedule> childSchedules = finder.getChildSchedules(tree);
			childSchedules.sort(null);
			
			//visited += childSchedules.size();
			TreeSchedule best = bestSoFar.get();
			for (TreeSchedule child : childSchedules) {
				// Only consider the child if its lower bound is better than current best
				if (best == null || child.getLowerBound() < best.getRuntime()) {
					if (child.isComplete()) {
						best = updateBest(child);
					} else {
						// Check if the child schedule is complete or not
						
						if(child.getAllocated().size() < child.getGraph().getAll().size() * 0.8) {
							invokeAll(new ForkJob(child));
						} else {
							compute(child);
						}
					}
				} else {
					break;
				}
			}
		}
		
		private TreeSchedule updateBest(TreeSchedule candinate) {
			TreeSchedule best = bestSoFar.get();
			while ((best == null ||candinate.getRuntime()<best.getRuntime()) && !bestSoFar.compareAndSet(best, candinate)) {
				best = bestSoFar.get();
			}
			return best;
		}
		
	}
	
	public ParallelBranchBoundSchedulingAlgorithm(ChildScheduleFinder finder, MinimumHeuristic heuristic, int parallelism) {
		this.parallelism=parallelism;
		pool = new ForkJoinPool(parallelism);
		this.finder = finder;
		this.heuristic = heuristic;
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) throws InterruptedException {
		bestSoFar = new AtomicReference<TreeSchedule>();
		TreeSchedule emptySchedule = new TreeSchedule(graph, heuristic, numberOfProcessors);
		ForkJob rootJob = new ForkJob(emptySchedule);
		pool.invoke(rootJob);
		
		return bestSoFar.get().getFullSchedule();
	}

}
