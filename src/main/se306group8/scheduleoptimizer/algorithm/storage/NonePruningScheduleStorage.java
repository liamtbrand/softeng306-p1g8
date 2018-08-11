package se306group8.scheduleoptimizer.algorithm.storage;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

public class NonePruningScheduleStorage implements ScheduleStorage {
	private final SchedulePriorityQueue queue;
	private final ScheduleArray array;
	
	private int maximumBound = Integer.MAX_VALUE;
	
	public NonePruningScheduleStorage(int blockSize) {
		array = new ScheduleArray(blockSize);
		queue = new SchedulePriorityQueue(array);
	}
	
	public NonePruningScheduleStorage() {
		this(100_000);
	}
	
	@Override
	public TreeSchedule pop() {		
		return array.get(queue.pop());
	}
	
	@Override
	public TreeSchedule peek() {
		return array.get(queue.peek());
	}
	
	@Override
	public void put(TreeSchedule schedule) {
		if(schedule.getLowerBound() > maximumBound) {
			return;
		}
		
		if(schedule.isComplete() && schedule.getRuntime() < maximumBound) {
			maximumBound = schedule.getRuntime();
		}
		
		int id = array.add(schedule);
		queue.put(id);
	}
	
	@Override
	public void pruneStorage(int maxBound) {
		if(maximumBound > maxBound)
			maximumBound = maxBound;
	}

	@Override
	public int size() {
		return array.size();
	}
}
