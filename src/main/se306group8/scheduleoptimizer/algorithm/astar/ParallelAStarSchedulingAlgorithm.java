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
	
	//kill all threads when they all agree its over
	private AtomicInteger doneVotes;
	
	private AtomicInteger upperBound;
	
	private final int numThreads;
	
	private TreeSchedule best;

	//currently not used need to find way of constructing these for each thread
	private ChildScheduleFinder childGenerator;

	private MinimumHeuristic heuristic;
	
	private class WorkerThread extends Thread {
		
		private final int numProcessors;
		
		private TreeSchedule threadBest;
		
		private boolean doneVote;
		
		public WorkerThread(int numProcessors) {
			this.numProcessors=numProcessors;
			this.doneVote=false;
		}

		@Override
		public void run() {
			
			//TODO make this plugable
			ChildScheduleFinder childGenerator = new DuplicateRemovingChildFinder(numProcessors);
			

			//only die when all threads want to die
			while (doneVotes.get() != numThreads) {
				
				//this pop may return null if the queue is empty it
				//can happen if items are taken faster than added
				threadBest = queue.pop();
				
				while (threadBest != null && threadBest.getLowerBound() < upperBound.get()) {
					
					//we said we were done but proved wrong
					if (doneVote) {
						doneVote = false;
						doneVotes.decrementAndGet();
					}
					
					if (threadBest.isComplete()) {
						checkBest(threadBest);
						break;
					}
							
					
					List<TreeSchedule> children = childGenerator.getChildSchedules(threadBest);

					queue.putAll(children);

					threadBest = queue.pop();
					
					getMonitor().setSolutionsExplored(queue.size());
				}
				
				//if we were not done before we are now
				if (!doneVote) {
					doneVote=true;
					doneVotes.incrementAndGet();
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
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		
		TreeSchedule greedySoln = best;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}
		
		//do not put greedy solution into the queue as it results in a race condition
		queue.put(best);
		
		//instead mark greedy solution here in global variable
		best = greedySoln;
		
		upperBound = new AtomicInteger(greedySoln.getRuntime());
		queue.pruneStorage(greedySoln.getRuntime());
		
		doneVotes = new AtomicInteger(0);
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
	//this method is synchronized to avoid race conditions
	private synchronized void checkBest(TreeSchedule threadBest) {
		assert(best != null && best.isComplete());
		if (best.getRuntime()>threadBest.getRuntime()) {
			best = threadBest;
			upperBound.set(best.getRuntime());
		}
	}

}
