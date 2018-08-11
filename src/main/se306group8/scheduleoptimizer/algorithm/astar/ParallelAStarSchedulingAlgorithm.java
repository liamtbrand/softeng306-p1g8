package se306group8.scheduleoptimizer.algorithm.astar;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.DuplicateRemovingChildFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
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
				TreeSchedule best = queue.pop();
				
				while (best != null && !best.isComplete() && !done.get()) {
					List<TreeSchedule> children = childGenerator.getChildSchedules(best);

					queue.putAll(children);

					best = queue.pop();
					
					getMonitor().setSolutionsExplored(queue.size());
				}
				
				if (best.isComplete()) {
					checkBest(best);
					done.set(true);
				}
			}
		}

	}
	
	public ParallelAStarSchedulingAlgorithm(int numThreads) {
		this.numThreads=numThreads;
		
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) {
		queue = new CocurrentObjectQueueScheduleStorage();
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		
		TreeSchedule greedySoln = best;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}
		
		queue.put(greedySoln);
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
	
	//multiple threads could get a compleate schedule at the same time check for sure
	private synchronized void checkBest(TreeSchedule threadBest) {
		if (best == null) {
			best = threadBest;
		}else if (best.getRuntime()>threadBest.getRuntime()) {
			best = threadBest;
		}
	}

}
