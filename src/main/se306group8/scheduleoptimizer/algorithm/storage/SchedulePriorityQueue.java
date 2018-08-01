package se306group8.scheduleoptimizer.algorithm.storage;

import se306group8.scheduleoptimizer.algorithm.InternalSchedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/** This is an implementation of a priority queue, that holds indexs into the ScheduleArray */
final class SchedulePriorityQueue {
	private final ScheduleArray array;
	
	SchedulePriorityQueue(TaskGraph graph) {
		this.array = new ScheduleArray(graph);
	}
	
	void put(InternalSchedule schedule) {
		// TODO Auto-generated method stub
	}

	InternalSchedule pop() {
		// TODO Auto-generated method stub
		return null;
	}
}
