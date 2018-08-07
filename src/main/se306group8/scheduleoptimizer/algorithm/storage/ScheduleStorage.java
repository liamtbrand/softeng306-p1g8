package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.Arrays;
import java.util.Collection;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

public class ScheduleStorage {
	private final SchedulePriorityQueue queue;
	
	/** Creates a schedule storage using the given minimum heuristic.
	 *
	 * @param minimum The minimum heuristic to use, this may not be null.
	 * @param sizeLimit The maximum number of Mb to use storing the schedules. More memory than this must not be used.
	 **/
	public ScheduleStorage(int sizeLimit) {
		queue = new SchedulePriorityQueue();
	}
	
	/** Stores a schedule in storage. */
	public void storeSchedule(TreeSchedule schedule) {
		queue.put(schedule);
	}
	
	/** Stores a list of schedules in storage. */
	public void storeSchedules(Collection<TreeSchedule> schedules) {
		for(TreeSchedule partial : schedules) {
			storeSchedule(partial);
		}
	}
	
	public TreeSchedule getBestSchedule() {
		return queue.poll();
	}
	
	public int size() {
		return queue.size();
	}
	
	public String toString() {
		return Arrays.deepToString(queue.toArray());
	}
}
