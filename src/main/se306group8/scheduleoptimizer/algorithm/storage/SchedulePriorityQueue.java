package se306group8.scheduleoptimizer.algorithm.storage;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/** This is an implementation of a priority queue, that holds indexs into the ScheduleArray */
final class SchedulePriorityQueue {
	private final ScheduleArray array;
	
	SchedulePriorityQueue(TaskGraph graph) {
		this.array = new ScheduleArray(graph);
	}
	
	void put(TreeSchedule schedule) {
		// TODO Auto-generated method stub
	}

	TreeSchedule pop() {
		// TODO Auto-generated method stub
		return null;
	}
}
