package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.PriorityQueue;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** Stores the schedules in a Java priority queue. */
public class ObjectQueueScheduleStorage implements ScheduleStorage {
	private final PriorityQueue<TreeSchedule> queue;
	
	private int maximumBound = Integer.MAX_VALUE;
	
	public ObjectQueueScheduleStorage() {
		queue = new PriorityQueue<>();
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
		if(schedule.getLowerBound() > maximumBound) {
			return;
		}
		
		if(schedule.isComplete() && schedule.getRuntime() < maximumBound) {
			maximumBound = schedule.getRuntime();
		}
		
		queue.add(schedule);
	}
	
	@Override
	public void pruneStorage(int maxBound) {
		if(maximumBound > maxBound)
			maximumBound = maxBound;
	}

	@Override
	public int size() {
		return queue.size();
	}
}
