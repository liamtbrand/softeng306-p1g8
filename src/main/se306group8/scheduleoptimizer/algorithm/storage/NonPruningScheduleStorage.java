package se306group8.scheduleoptimizer.algorithm.storage;

import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

public class NonPruningScheduleStorage implements ScheduleStorage {
	private final SchedulePriorityQueue queue;
	private final ScheduleArray array;
	private TreeSchedule bestComplete = null;
	private int maximumBound = Integer.MAX_VALUE;
	
	public NonPruningScheduleStorage(int blockSize) {
		array = new ScheduleArray(blockSize);
		queue = new SchedulePriorityQueue(array);
	}
	
	public NonPruningScheduleStorage() {
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
		if(schedule.getLowerBound() >= maximumBound) {
			return;
		}
		
		if(schedule.isComplete() && schedule.getRuntime() < maximumBound) {
			pruneStorage(schedule.getRuntime());
			bestComplete = schedule;
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

	@Override
	public void signalMonitor(RuntimeMonitor monitor) {
		monitor.setSchedulesInArray(array.size());
		monitor.setSchedulesInQueue(queue.size() - array.size());
	}

	@Override
	public TreeSchedule getBestSchedule() {
		return bestComplete;
	}
}
