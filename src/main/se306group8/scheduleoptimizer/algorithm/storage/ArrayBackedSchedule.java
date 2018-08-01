package se306group8.scheduleoptimizer.algorithm.storage;

import se306group8.scheduleoptimizer.algorithm.InternalSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/** This represents an internal schedule that is stored in a schedule array object. The children returned by this object
 * may or may not be instances of this class. */
final class ArrayBackedSchedule extends InternalSchedule {
	private final int index;
	
	ArrayBackedSchedule(TaskGraph graph, ScheduleArray array, int index, int parent, Task task, int processor, int lowerBound) {
		super(graph, array.getSchedule(parent), task, processor, lowerBound);
		
		this.index = index;
	}

	int getIndex() {
		return index;
	}
}
