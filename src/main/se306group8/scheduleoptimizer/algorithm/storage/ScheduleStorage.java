package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.Collection;

import se306group8.scheduleoptimizer.algorithm.InternalSchedule;
import se306group8.scheduleoptimizer.algorithm.MinimumHeuristic;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class ScheduleStorage {
	private final SchedulePriorityQueue queue;
	
	/** Creates a schedule storage using the given minimum heuristic.
	 *
	 * @param minimum The minimum heuristic to use, this may not be null.
	 * @param sizeLimit The maximum number of Mb to use storing the schedules. More memory than this must not be used.
	 **/
	public ScheduleStorage(TaskGraph graph, MinimumHeuristic minimum, int sizeLimit) {
		queue = new SchedulePriorityQueue(graph);
	}
	
	/** Stores a schedule in storage. */
	public void storeSchedule(InternalSchedule schedule) {
		queue.put(schedule);
	}
	
	/** Stores a list of schedules in storage. */
	public void storeSchedules(Collection<InternalSchedule> schedules) {
		for(InternalSchedule partial : schedules) {
			storeSchedule(partial);
		}
	}
	
	public InternalSchedule getBestSchedule() {
		return queue.pop();
	}
}
