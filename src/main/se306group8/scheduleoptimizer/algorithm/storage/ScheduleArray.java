package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.ArrayList;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** This class represents a linear array of schedules. All of these schedules must be generated from the
 * same root schedule. */
class ScheduleArray {
	final int blockSize;
	
	private int size = 0;
	
	/** This class represents a large block of schedules. This represents a storage of about 1Mb.
	 * This block size is chosen to be small enough so as to not waste memory. But large enough so
	 * that the object overheads are largely irrelevant. */
	class ScheduleBlock {
		int size = 0;
		final int slot;
		
		/** Stores the array of parent schedules */
		final int[] parentsArray = new int[blockSize];
		/** Stores what processor each schedule places the task on */
		final byte[] processorArray = new byte[blockSize];
		/** Stores the task that each schedule allocates */
		final byte[] taskArray = new byte[blockSize];
		/** Pre-computes the result of the heuristic */
		final short[] lowerBound = new short[blockSize];
		/** Counts the number of tasks that have been scheduled on this schedule */
		final byte[] tasks = new byte[blockSize];
		
		ScheduleBlock(int slot) {
			this.slot = slot;
		}
		
		/** Adds a schedule to the block and returns the index it was given. This index is the index that should
		 * be used to retrieve it from the array. */
		int add(TreeSchedule schedule) {
			int index = size;
			size++;
			
			lowerBound[index] = (short) schedule.getLowerBound();
			parentsArray[index] = ScheduleArray.this.add(schedule.getParent());
			taskArray[index] = (byte) schedule.getMostRecentAllocation().task.getId();
			processorArray[index] = (byte) schedule.getMostRecentAllocation().processor;
			tasks[index] = (byte) schedule.getAllocated().size();
			
			return index + slot * blockSize;
		}

		boolean isFull() {
			return size >= blockSize;
		}

		void remove() {
			blocks.set(slot, null);
			ScheduleArray.this.size -= size;
		}
		
		@Override
		public String toString() {
			return "(" + size + ")";
		}
	}
	
	/** Each block stores a large number of schedules at 8B per schedule. This limits the cost of resizing the array. */
	private final ArrayList<ScheduleBlock> blocks = new ArrayList<>();
	
	private TreeSchedule rootSchedule;
	
	ScheduleArray(int blockSize) {
		this.blockSize = blockSize;
	}
	
	int getLowerBound(int id) {
		if(id == -1) {
			return rootSchedule.getLowerBound();
		}
		
		ScheduleBlock block = blocks.get(id / blockSize);
		int subIndex = id % blockSize;
		
		return block.lowerBound[subIndex];
	}
	
	int getNumberOfTasks(int id) {
		if(id == -1) {
			return 0;
		}
		
		ScheduleBlock block = blocks.get(id / blockSize);
		int subIndex = id % blockSize;
		
		return block.tasks[subIndex];
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
		
		ScheduleBlock block = blocks.get(id / blockSize);
		int subIndex = id % blockSize;
		
		int parent = block.parentsArray[subIndex];
		int task = Byte.toUnsignedInt(block.taskArray[subIndex]);
		int processor = Byte.toUnsignedInt(block.processorArray[subIndex]);
		int lowerBound = Short.toUnsignedInt(block.lowerBound[subIndex]);
		
		return new ArrayBackedSchedule(this, id, parent, rootSchedule.getGraph().getTask(task), processor, lowerBound);
	}
	
	/** Adds a schedule to the array. If this schedule object was already in the array it is not re-added
	 * and the index is returned.
	 * 
	 * @throws OutOfMemoryError if there are too many schedules in this array.
	 * @return the id that was allocated to this object.
	 **/
	int add(TreeSchedule schedule) throws OutOfMemoryError {
		if(schedule.getParent() == null) {
			assert rootSchedule == null || rootSchedule.equals(schedule);
			
			if(rootSchedule == null) {
				size++;
				rootSchedule = schedule;
			}
			
			return -1;
		}
		
		if(schedule instanceof ArrayBackedSchedule) {
			return ((ArrayBackedSchedule) schedule).getIndex();
		} else {
			ScheduleBlock block = getBlockFor(schedule);
			
			size++;
			return block.add(schedule);
		}
	}
	
	ScheduleBlock getBlockFor(TreeSchedule schedule) {
		if(blocks.isEmpty()) {
			return allocateNewBlock();
		}
		
		ScheduleBlock block = blocks.get(blocks.size() - 1);
		if(block == null || block.isFull()) {
			return allocateNewBlock();
		}
		
		return block;
	}
	
	ScheduleBlock allocateNewBlock() {
		ScheduleBlock block = new ScheduleBlock(blocks.size());
		blocks.add(block);
		
		return block;
	}
	
	int size() {
		return size;
	}
}
