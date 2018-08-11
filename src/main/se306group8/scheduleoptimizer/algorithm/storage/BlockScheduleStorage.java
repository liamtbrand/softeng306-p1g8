package se306group8.scheduleoptimizer.algorithm.storage;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** This class represents a compressed collection of schedules. It has support for querying the best, clearing the un-needed and other actions. */
public class BlockScheduleStorage implements ScheduleStorage {
	private final SchedulePriorityQueue queue;
	private final BlockScheduleArray array;
	
	public BlockScheduleStorage(int granularity, int blockSize) {
		array = new BlockScheduleArray(100_000, 20);
		queue = new SchedulePriorityQueue(array);
	}
	
	public BlockScheduleStorage() {
		this(100_000, 20);
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
		
		int id = array.add(schedule);
		
		if(schedule.getLowerBound() < array.getEndOfQueue()) {
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
}
