package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.ArrayList;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/** This class represents a linear array of schedules */
class ScheduleArray {
	private static final int BLOCK_SIZE = 100_000;
	
	/** This class represents a large block of schedules. This represents a storage of about 1Mb.
	 * This block size is chosen to be small enough so as to not waste memory. But large enough so
	 * that the object overheads are largely irrelevant. */
	private class ScheduleBlock {
		
		/** Stores the array of parent schedules */
		final int[] parentsArray = new int[BLOCK_SIZE];
		/** Stores what processor each schedule places the task on */
		final byte[] processorArray = new byte[BLOCK_SIZE];
		/** Stores the task that each schedule allocates */
		final byte[] taskArray = new byte[BLOCK_SIZE];
		/** Pre-computes the result of the heuristic */
		final short[] lowerBound = new short[BLOCK_SIZE];
		
		void setSchedule(int subIndex, TreeSchedule schedule) {
			lowerBound[subIndex] = (short) schedule.getLowerBound();
			parentsArray[subIndex] = addSchedule(schedule.getParent());
			taskArray[subIndex] = (byte) schedule.getMostRecentTask().getId();
			processorArray[subIndex] = (byte) schedule.getMostRecentProcessor();
		}
	}
	
	/** Each block stores a large number of schedules at 8B per schedule. This limits the cost of resizing the array. */
	private final ArrayList<ScheduleBlock> blocks = new ArrayList<>();
	private final TaskGraph graph;
	
	private int nextId = 0;
	
	ScheduleArray(TaskGraph graph) {
		this.graph = graph;
	}
	
	int getLowerBound(int id) {
		ScheduleBlock block = blocks.get(id / BLOCK_SIZE);
		int subIndex = id % BLOCK_SIZE;
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
		
		ScheduleBlock block = blocks.get(id / BLOCK_SIZE);
		int subIndex = id % BLOCK_SIZE;
		
		int parent = block.parentsArray[subIndex];
		int task = Byte.toUnsignedInt(block.taskArray[subIndex]);
		int processor = Byte.toUnsignedInt(block.processorArray[subIndex]);
		int lowerBound = Short.toUnsignedInt(block.lowerBound[subIndex]);
		
		return new ArrayBackedSchedule(graph, this, id, parent, graph.getTask(task), processor, lowerBound);
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
			int index = nextId++;
			ScheduleBlock block = getBlockCheckingForBounds(index / BLOCK_SIZE);
			int subIndex = index % BLOCK_SIZE;
			
			block.setSchedule(subIndex, schedule);
			
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
