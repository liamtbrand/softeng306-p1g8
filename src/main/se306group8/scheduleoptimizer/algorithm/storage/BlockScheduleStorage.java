package se306group8.scheduleoptimizer.algorithm.storage;

import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** This class represents a compressed collection of schedules. It has support for querying the best, clearing the un-needed and other actions. */
public class BlockScheduleStorage implements ScheduleStorage {
	private final SchedulePriorityQueue queue;
	private final BlockScheduleArray array;
	
	/**
	 * Creates a blocked schedule storage
	 * @param granularity This is the width of the buckets that the schedules are stored in based on their lower bound.
	 * If this is 10, then all of the schedules with lower bounds from 0-9 will be stored together, 10-19, 20-29 ...
	 * @param blockSize The size of the smallest allocatable unit of memory, in schedules.
	 */
	public BlockScheduleStorage(int granularity, int blockSize) {
		array = new BlockScheduleArray(blockSize, granularity);
		queue = new SchedulePriorityQueue(array);
	}
	
	public BlockScheduleStorage() {
		this(20, 100_000);
	}
	
	@Override
	public TreeSchedule pop() {
		while(queue.size() == 0) {
			array.addNextWidthTo(queue);
		}
		
		//This is safe as the block that is in the queue should never be pruned.
		return array.get(queue.pop());
	}
	
	@Override
	public TreeSchedule peek() {
		while(queue.size() == 0) {
			array.addNextWidthTo(queue);
		}
		
		return array.get(queue.peek());
	}
	
	@Override
	public void put(TreeSchedule schedule) {
		if(schedule.getLowerBound() > array.getPruneMaximum()) {
			return;
		}
		
		if(schedule.isComplete()) {
			array.setPruneMaximum(schedule.getRuntime());
		}
		
		boolean addedToQueue = schedule.getLowerBound() < array.getEndOfQueue();
		
		int id = array.add(schedule, !addedToQueue); //If we did not add it later, flag it for addition now
		if(addedToQueue) {
			queue.put(id);
		}
	}
	
	@Override
	public void pruneStorage(int maxBound) {
		array.setPruneMaximum(maxBound);
	}

	@Override
	public int size() {
		return array.size();
	}

	@Override
	public void signalMonitor(RuntimeMonitor monitor) {
		monitor.setSchedulesInArray(array.size() - queue.size());
		monitor.setSchedulesInQueue(queue.size());
	}
}
