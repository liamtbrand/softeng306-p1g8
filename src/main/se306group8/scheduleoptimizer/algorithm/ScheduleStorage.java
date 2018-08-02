package se306group8.scheduleoptimizer.algorithm;

import java.util.Collection;

import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class ScheduleStorage {
	/** Creates a schedule storage using the given minimum heuristic.
	 *
	 * @param minimum The minimum heuristic to use, this may not be null.
	 * @param sizeLimit The maximum number of Mb to use storing the schedules. More memory than this must not be used.
	 **/
	public ScheduleStorage(MinimumHeuristic minimum, int sizeLimit) {
		assert false : "Not done";
	}
	
	/** Stores a schedule in storage. */
	public void storeSchedule(TreeSchedule schedule) {
		assert false : "Not done";
	}
	
	/** Stores a list of schedules in storage. */
	public void storeSchedules(Collection<TreeSchedule> schedules) {
		for(TreeSchedule partial : schedules) {
			storeSchedule(partial);
		}
	}
	
	public Schedule getBestSchedule() {
		assert false : "Not done";
		return null;
	}
}
