package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** Stores the schedules in a Java PriorityBlockingQueue. */
public class CocurrentObjectQueueScheduleStorage implements ScheduleStorage {
	private final PriorityBlockingQueue<TreeSchedule> queue;
	
	private AtomicInteger maximumBound = new AtomicInteger(Integer.MAX_VALUE);
	
	public CocurrentObjectQueueScheduleStorage() {
		queue = new PriorityBlockingQueue<>();
	}
	
	@Override
	public TreeSchedule pop() {		
		return queue.poll();
	}
	
	@Override
	public TreeSchedule peek() {
		return queue.peek();
	}
	
	@Override
	public void put(TreeSchedule schedule) {
		if(schedule.getLowerBound() > maximumBound.get()) {
			return;
		}
		
		if(schedule.isComplete() && schedule.getRuntime() < maximumBound.get()) {
			updateBound(schedule.getRuntime());
		}
		
		queue.add(schedule);
	}
	
	@Override
	public void pruneStorage(int maxBound) {
		updateBound(maxBound);
	}

	@Override
	public int size() {
		return queue.size();
	}
	
	private void updateBound(int newbound) {
		int oldBound = maximumBound.get();
		
		//to avoid race conditions we use compareAndSet to ensure we have the 
		//up to date value of maximumBound
		while (oldBound > newbound && maximumBound.compareAndSet(oldBound, newbound)) {
			oldBound = maximumBound.get();
		}
	}
}
