package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.Collection;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

public interface ScheduleStorage {
	/** Stores a schedule in storage. */
	void storeSchedule(TreeSchedule schedule);

	/** Stores a list of schedules in storage. */
	void storeSchedules(Collection<TreeSchedule> schedules);

	TreeSchedule getBestSchedule();

	int size();

	String toString();
	
	/** Prunes all solutions that have a lower bound larger than this number. */
	default void pruneSolutions(int maximum) {  }
}
