package se306group8.scheduleoptimizer.algorithm.astar;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.RuntimeErrorException;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.DuplicateRemovingChildFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.storage.CocurrentObjectQueueScheduleStorage;
import se306group8.scheduleoptimizer.algorithm.storage.ScheduleStorage;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class ParallelAStarSchedulingAlgorithm extends Algorithm{
	
	private ScheduleStorage queue;
	
	//used to kill all threads
	private AtomicBoolean done;
	
	
	private final int numThreads;
	
	private TreeSchedule best;

	private ChildScheduleFinder childGenerator;

	private MinimumHeuristic heuristic;
	
	private class WorkerThread extends Thread {
		
		private final int numProcessors;
		
		private TreeSchedule threadBest;
		
		public WorkerThread(int numProcessors) {
			this.numProcessors=numProcessors;
		}

		@Override
		public void run() {
			
			//TODO make this plugable
			ChildScheduleFinder childGenerator = new DuplicateRemovingChildFinder(numProcessors);
			
			while (!done.get()) {
				TreeSchedule threadBest = queue.pop();
				
				while (threadBest != null && !threadBest.isComplete() && !done.get()) {
					List<TreeSchedule> children = childGenerator.getChildSchedules(threadBest);

					queue.putAll(children);

					threadBest = queue.pop();
					
					getMonitor().setSolutionsExplored(queue.size());
				}
				
				if (threadBest != null && threadBest.isComplete()) {
					checkBest(threadBest);
					done.set(true);
				}
			}
			
		}

	}
	
	public ParallelAStarSchedulingAlgorithm(int numThreads,ChildScheduleFinder childGenerator, MinimumHeuristic heuristic) {
		this.numThreads=numThreads;
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
		
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) {
		queue = new CocurrentObjectQueueScheduleStorage();
		best = new TreeSchedule(graph, heuristic);
		done = new AtomicBoolean(false);
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		
		TreeSchedule greedySoln = best;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}
		
		queue.put(best);
		best = greedySoln;
		queue.pruneStorage(greedySoln.getRuntime());
		
		
		Thread[] threads = new Thread[numThreads];
		for (int i=0;i<numThreads;i++) {
			threads[i]= new WorkerThread(numberOfProcessors);
			threads[i].start();
		}
		
		//wait for all threads to DIE!!
		for (int i=0;i<numThreads;i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// Something has gone terribly wrong
				e.printStackTrace();
			}
		}
		
		return best.getFullSchedule();
	}
	
	//multiple threads could get a complete schedule at the same time check for sure
	private synchronized void checkBest(TreeSchedule threadBest) {
		if (best == null) {
			best = threadBest;
		}else if (best.getRuntime()>threadBest.getRuntime()) {
			best = threadBest;
		}
	}

}
