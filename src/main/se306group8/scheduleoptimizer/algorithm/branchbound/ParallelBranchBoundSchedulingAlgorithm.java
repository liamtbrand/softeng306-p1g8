package se306group8.scheduleoptimizer.algorithm.branchbound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class ParallelBranchBoundSchedulingAlgorithm extends Algorithm{
	
	private ForkJoinPool pool;
	private final int parallelism;
	private AtomicReference<TreeSchedule> bestSoFar;
	private AtomicInteger explored;
	
	private final ChildScheduleFinder finder;
	private final MinimumHeuristic heuristic;
	
	private class ForkJob extends RecursiveAction {

		private static final long serialVersionUID = 1L;
		private static final int TASKS_TO_FORK = 4;

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
			
			TreeSchedule best = bestSoFar.get();
			
			boolean doFork = tree.getGraph().getAll().size() - tree.getAllocated().size() >= TASKS_TO_FORK && tree.getAllocated().size() >= 2; //We don't split the top bit so we explore the good parts of the solution space first.
			
			List<ForkJob> jobs = new ArrayList<ForkJob>();
			
			explored.addAndGet(childSchedules.size());
			getMonitor().setSchedulesExplored(explored.get());
			
			for (TreeSchedule child : childSchedules) {
				// Only consider the child if its lower bound is better than current best
				if (child.getLowerBound() < best.getRuntime()) {
					if (child.isComplete()) {
						best = updateBest(child);
						
					} else {
						// Check if the child schedule is complete or not
						
						if(doFork && getSurplusQueuedTaskCount() < 10) { //Don't split unless there are fewer than 10 subtasks left in the queue.
							ForkJob job = new ForkJob(child);
							job.fork();
							jobs.add(job);
						} else {
							compute(child);
						}
					}
				} else {
					break;
				}
			}
			
			for(ForkJob job : jobs) {
				job.join();
			}
		}
		
		/**
		 * Updates the best complete schedule in a thread safe manor
		 * it also informs the runtime monitor
		 */
		private TreeSchedule updateBest(TreeSchedule candinate) {
						
			TreeSchedule best;
			boolean validSet = false;
			do {
				best = bestSoFar.get();
				if (candinate.getRuntime()<best.getRuntime()) {
					
					//CompareAndSet ensures we don't have race conditions on write
					validSet = bestSoFar.compareAndSet(best, candinate);
				} else {
					break;
				}
			//If we have to loop it means another thread updated best
			//we have to check again if we are still better
			} while (!validSet);
			
			//inform the monitor if we updated
			if (validSet) {
				getMonitor().updateBestSchedule(bestSoFar.get());
			}
			return best;
		}
		
	}
	
	public ParallelBranchBoundSchedulingAlgorithm(ChildScheduleFinder finder, MinimumHeuristic heuristic,RuntimeMonitor monitor, int parallelism) {
		super(monitor);
		this.parallelism=parallelism;
		this.finder = finder;
		this.heuristic = heuristic;
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) throws InterruptedException {
		getMonitor().setNumberOfProcessors(numberOfProcessors);
		
		pool = new ForkJoinPool(parallelism);
		
		TreeSchedule emptySchedule = new TreeSchedule(graph, heuristic, numberOfProcessors);
		ForkJob rootJob = new ForkJob(emptySchedule);
		explored = new AtomicInteger();
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		TreeSchedule greedySoln = emptySchedule;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}
		bestSoFar = new AtomicReference<TreeSchedule>(greedySoln);
		
		
		pool.invoke(rootJob);
		
		return bestSoFar.get().getFullSchedule();
	}

}
