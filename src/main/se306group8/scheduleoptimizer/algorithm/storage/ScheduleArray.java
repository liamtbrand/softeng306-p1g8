package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.ArrayList;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** This class represents a linear array of schedules. All of these schedules must be generated from the
 * same root schedule. */
class ScheduleArray {
	static final int BLOCK_SIZE = 100_000;
	
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
			parentsArray[subIndex] = addOrGetId(schedule.getParent());
			taskArray[subIndex] = (byte) schedule.getMostRecentTask().getId();
			processorArray[subIndex] = (byte) schedule.getMostRecentProcessor();
		}
	}
	
	/** Each block stores a large number of schedules at 8B per schedule. This limits the cost of resizing the array. */
	private final ArrayList<ScheduleBlock> blocks = new ArrayList<>();
	private TreeSchedule rootSchedule;
	
	private int nextId = 0;
	
	int getLowerBound(int id) {
		if(id == -1) {
			return rootSchedule.getLowerBound();
		}
		
		ScheduleBlock block = blocks.get(id / BLOCK_SIZE);
		int subIndex = id % BLOCK_SIZE;
		return block.lowerBound[subIndex];
	}
	
	/**
	 * Returns the schedule at the given id.
	 * 
	 * @param id The id to retrieve a schedule at.
	 * @return The schedule
	 */
	TreeSchedule get(int id) {
		if(id == -1) {
			assert rootSchedule != null;
			
			return rootSchedule;
		}
		
		ScheduleBlock block = blocks.get(id / BLOCK_SIZE);
		int subIndex = id % BLOCK_SIZE;
		
		int parent = block.parentsArray[subIndex];
		int task = Byte.toUnsignedInt(block.taskArray[subIndex]);
		int processor = Byte.toUnsignedInt(block.processorArray[subIndex]);
		int lowerBound = Short.toUnsignedInt(block.lowerBound[subIndex]);
		
		return new ArrayBackedSchedule(rootSchedule.getGraph(), this, id, parent, rootSchedule.getGraph().getTask(task), processor, lowerBound);
	}
	
	/** Adds a schedule to the array. If this schedule object was already in the array it is not re-added
	 * and the index is returned.
	 * 
	 * @throws OutOfMemoryError if there are too many schedules in this array.
	 * @return the id that was allocated to this object.
	 **/
	int addOrGetId(TreeSchedule schedule) throws OutOfMemoryError {
		if(schedule.getParent() == null) {
			assert rootSchedule == null || rootSchedule.equals(schedule);
			
			rootSchedule = schedule; //Update root, this may be the first time.
			return -1;
		}
		
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
