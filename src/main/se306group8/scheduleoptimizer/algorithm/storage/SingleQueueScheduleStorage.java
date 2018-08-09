package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.Arrays;
import java.util.Collection;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

public class SingleQueueScheduleStorage implements ScheduleStorage {
	private final SchedulePriorityQueue queue;
	
	/** Creates a schedule storage with the given maximum size
	 * @param sizeLimit The maximum number of Mb to use storing the schedules. More memory than this must not be used.
	 **/
	public SingleQueueScheduleStorage(int sizeLimit) {
		queue = new SchedulePriorityQueue();
	}
	
	/** Stores a schedule in storage. */
	@Override
	public void storeSchedule(TreeSchedule schedule) {
		queue.put(schedule);
	}
	
	/** Stores a list of schedules in storage. */
	@Override
	public void storeSchedules(Collection<TreeSchedule> schedules) {
		for(TreeSchedule partial : schedules) {
			storeSchedule(partial);
		}
	}
	
	@Override
	public TreeSchedule getBestSchedule() {
		return queue.poll();
	}
	
	@Override
	public int size() {
		return queue.size();
	}
	
	@Override
	public String toString() {
		return Arrays.deepToString(queue.toArray());
	}
}
