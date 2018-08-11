package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.Collection;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

public interface ScheduleStorage {

	/** Returns and removes the best schedule from the storage */
	TreeSchedule pop();

	/** Returns the best schedule from the storage */
	TreeSchedule peek();

	/** Places a schedule into storage. */
	void put(TreeSchedule schedule);
	
	/** Places many schedules into storage. */
	default void putAll(Collection<? extends TreeSchedule> schedules) {
		for(TreeSchedule s : schedules) {
			put(s);
		}
	}

	/** Deletes all schedules with a bound greater than the maximum.
	 * This method makes a best effort, and may not actually delete the schedules.
	 * They will not be returned by pop or peek. */
	void pruneStorage(int maxBound);

	int size();
}