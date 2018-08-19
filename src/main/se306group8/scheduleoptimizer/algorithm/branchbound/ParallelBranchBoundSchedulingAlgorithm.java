package se306group8.scheduleoptimizer.algorithm.branchbound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * A parallel algorithm to find optimal schedules. It uses depth first search
 * branch and bound. Internally it uses the ForkJoin framework.
 */
public class ParallelBranchBoundSchedulingAlgorithm extends Algorithm{
	
	private ForkJoinPool pool;
	private final int parallelism; //Max thread
	
	//These variables are updated in the threads so we have them atomic
	private AtomicReference<TreeSchedule> bestSoFar;
	private AtomicLong explored;
	
	private final ChildScheduleFinder finder;
	private final MinimumHeuristic heuristic;
	
	/**
	 * Worker threads that create other workers. These workers will operate when a
	 * thread becomes available.
	 */
	private class ForkJob extends RecursiveAction {

		private static final long serialVersionUID = 1L;
		
		//Constant used to control forking rate. 
		private static final int TASKS_TO_FORK = 4;

		private TreeSchedule jobSchedule;
		
		//Constructor takes the schedule to start search one
		public ForkJob(TreeSchedule job) {
			jobSchedule=job;
		}

		@Override
		protected void compute() {
			compute(jobSchedule);
		}
		
		private void compute(TreeSchedule tree) {
			if (getMonitor().isInterupted()) {
				return;
			}
			List<TreeSchedule> childSchedules = finder.getChildSchedules(tree);
			childSchedules.sort(null);
			
			TreeSchedule best = bestSoFar.get();
			
			//Forking too often reduces performance we want to go deep for pruning
			boolean doFork = tree.getGraph().getAll().size() - tree.getAllocated().size() >= TASKS_TO_FORK && tree.getAllocated().size() >= 2; //We don't split the top bit so we explore the good parts of the solution space first.
			
			List<ForkJob> jobs = new ArrayList<ForkJob>();
			
			//Atomically increment the explored counter for the runtimemonitor. 
			explored.addAndGet(childSchedules.size());
			getMonitor().setSchedulesExplored(explored.get());
			
			for (TreeSchedule child : childSchedules) {
				// Only consider the child if its lower bound is better than current best
				if (child.getLowerBound() < best.getRuntime()) {
					if (child.isComplete()) {
						best = updateBest(child);
						getMonitor().setUpperBound(child.getRuntime());
					} else {
						// Check if the child schedule is complete or not
						getMonitor().updateBestSchedule(child);
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
	protected int getNumberOfCores() {
		return parallelism;
	}
	
	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) throws InterruptedException {
		
		//all the ForkJobs must run in these threads
		pool = new ForkJoinPool(parallelism);
		TreeSchedule emptySchedule = new TreeSchedule(graph, heuristic, numberOfProcessors);
		ForkJob rootJob = new ForkJob(emptySchedule);
		explored = new AtomicInteger();
		
		//calc greedy soln for upperbound
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		TreeSchedule greedySoln = emptySchedule;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}
	
		getMonitor().setUpperBound(greedySoln.getRuntime());
		bestSoFar = new AtomicReference<TreeSchedule>(greedySoln);
		
		//this method is blocking until all workers are dead
		pool.invoke(rootJob);
		getMonitor().updateBestSchedule(bestSoFar.get());
		
		if (getMonitor().isInterupted()) {
			throw new InterruptedException();
		}
		
		return bestSoFar.get().getFullSchedule();
		
		
	}

	@Override
	public String toString() {
		return "DFS Branch & Bound";
	}

}
