package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/** This class represents a linear array of schedules */
class ScheduleArray {
	/** This class represents a large block of schedules. This represents a storage of about 1Mb.
	 * This block size is chosen to be small enough so as to not waste memory. But large enough so
	 * that the object overheads are largely irrelevant. */
	private static class ScheduleBlock {
		static final int BLOCK_SIZE = 100_000;
		
		/** Stores the array of parent schedules */
		final int[] parentsArray = new int[BLOCK_SIZE];
		/** Stores what processor each schedule places the task on */
		final byte[] processorArray = new byte[BLOCK_SIZE];
		/** Stores the task that each schedule allocates */
		final byte[] taskArray = new byte[BLOCK_SIZE];
		/** Pre-computes the result of the heuristic */
		final short[] lowerBound = new short[BLOCK_SIZE];
	}
	
	/** Each block stores a large number of schedules at 8B per schedule. This limits the cost of resizing the array. */
	private final ArrayList<ScheduleBlock> blocks = new ArrayList<>();
	private final TaskGraph graph;
	
	private int nextId = 0;
	
	private final Task[] integerToTask;
	private final Map<Task, Integer> taskToInteger;
	
	ScheduleArray(TaskGraph graph) {
		this.graph = graph;
		
		integerToTask = graph.getAll().toArray(new Task[0]);
		taskToInteger = new HashMap<>();
		
		for(int i = 0; i < integerToTask.length; i++) {
			taskToInteger.put(integerToTask[i], i);
		}
	}
	
	int getLowerBound(int id) {
		ScheduleBlock block = blocks.get(id / ScheduleBlock.BLOCK_SIZE);
		int subIndex = id % ScheduleBlock.BLOCK_SIZE;
		return block.lowerBound[subIndex];
	}
	
	/**
	 * Returns the schedule at the given id.
	 * 
	 * @param id The id to retrieve a schedule at.
	 * @return The schedule, or null if the id is -1
	 */
	ArrayBackedSchedule getSchedule(int id) {
		if(id == -1) {
			return null;
		}
		
		ScheduleBlock block = blocks.get(id / ScheduleBlock.BLOCK_SIZE);
		int subIndex = id % ScheduleBlock.BLOCK_SIZE;
		
		int parent = block.parentsArray[subIndex];
		int task = Byte.toUnsignedInt(block.taskArray[subIndex]);
		int processor = Byte.toUnsignedInt(block.processorArray[subIndex]);
		int lowerBound = Short.toUnsignedInt(block.lowerBound[subIndex]);
		
		return new ArrayBackedSchedule(graph, this, id, parent, getTask(task), processor, lowerBound);
	}
	
	private Task getTask(int task) {
		return integerToTask[task];
	}

	private byte getTaskID(Task task) {
		return taskToInteger.get(task).byteValue();
	}
	
	/** Adds a schedule to the array. If this schedule object was already in the array it is not re-added
	 * and the index is returned.
	 * 
	 * @throws OutOfMemoryError if there are too many schedules in this array.
	 * @return the id that was allocated to this object.
	 **/
	int addSchedule(TreeSchedule schedule) throws OutOfMemoryError {
		if(schedule == null)
			return -1;
		
		if(schedule instanceof ArrayBackedSchedule) {
			return ((ArrayBackedSchedule) schedule).getIndex();
		} else {
			int parentIndex = addSchedule(schedule);
			
			int index = nextId++;
			ScheduleBlock block = getBlockCheckingForBounds(index / ScheduleBlock.BLOCK_SIZE);
			int subIndex = index % ScheduleBlock.BLOCK_SIZE;
			
			block.lowerBound[subIndex] = (short) schedule.getLowerBound();
			block.parentsArray[subIndex] = parentIndex;
			block.taskArray[subIndex] = getTaskID(schedule.getMostRecentTask());
			block.processorArray[subIndex] = (byte) schedule.getMostRecentProcessor();
			
			return index;
		}
	}

	/** Gets a block of schedules, also checking for out of bounds and creating the block if it does not exist. */
	private ScheduleBlock getBlockCheckingForBounds(int i) {
		while(i >= blocks.size()) {
			blocks.add(new ScheduleBlock());
		}
		
		return blocks.get(i);
	}
}
