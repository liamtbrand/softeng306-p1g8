package se306group8.scheduleoptimizer.algorithm.storage;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;

/** This represents an internal schedule that is stored in a schedule array object. The children returned by this object
 * may or may not be instances of this class. */
final class ArrayBackedSchedule extends TreeSchedule {
	private final int index;
	
	ArrayBackedSchedule(ScheduleArray scheduleStorage, int index, int parent, Task task, int processor) {
		super(task, processor, scheduleStorage.get(parent));
		
		this.index = index;
	}

	int getIndex() {
		return index;
	}
}
