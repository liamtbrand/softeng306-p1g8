package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** This class represents a compressed collection of schedules. It has support for querying the best, clearing the un-needed and other actions. */
public class ScheduleStorage {
	private TreeSchedule rootSchedule;
	
	/** This class represents a large block of schedules. This represents a storage of about 1Mb.
	 * This block size is chosen to be small enough so as to not waste memory. But large enough so
	 * that the object overheads are largely irrelevant. */
	private class ScheduleBlock {
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
		
		ScheduleBlock(int slot) {
			this.slot = slot;
		}
		
		int add(TreeSchedule schedule) {
			int index = size;
			size++;
			
			lowerBound[index] = (short) schedule.getLowerBound();
			parentsArray[index] = addOrGetId(schedule.getParent());
			taskArray[index] = (byte) schedule.getMostRecentAllocation().task.getId();
			processorArray[index] = (byte) schedule.getMostRecentAllocation().processor;
			
			return index + slot * blockSize;
		}

		boolean isFull() {
			return size >= blockSize;
		}
	}
	
	/** Each block stores a large number of schedules at 8B per schedule. This limits the cost of resizing the array. */
	private final List<ScheduleBlock> blocks = new ArrayList<>();
	
	private final int granularity;
	private final int blockSize;
	
	/** Each array is a list of blocks that are assigned to the width going from i * granularity to (i + 1) * granularity */
	private final List<List<ScheduleBlock>> widths = new ArrayList<>();
	private final SchedulePriorityQueue queue = new SchedulePriorityQueue(this);
	
	private int size = 0;
	
	private int maximumBound = Integer.MAX_VALUE;
	
	/** Schedules in this width have greater lower bounds than all currently in the queue. */
	private int widthIndexNotInQueue = 0;
	
	public ScheduleStorage() {
		this(10, 100_000);
	}
	
	public ScheduleStorage(int granularity, int blockSize) {
		this.granularity = granularity;
		this.blockSize = blockSize;
	}
	
	public TreeSchedule pop() {
		while(queue.size() == 0) {
			populateQueue();
		}
		
		return get(queue.pop());
	}
	
	public TreeSchedule peek() {
		while(queue.size() == 0) {
			populateQueue();
		}
		
		return get(queue.peek());
	}
	
	/** Adds a new block of ids to the queue if needed */
	private void populateQueue() {
		for(ScheduleBlock block : widths.get(widthIndexNotInQueue)) {
			int start = block.slot * blockSize;
			int end = start + block.size;
			
			for(int i = start; i < end; i++) {
				queue.put(i);
			}
		}
		
		widthIndexNotInQueue++;
	}

	public void put(TreeSchedule schedule) {
		if(schedule.getLowerBound() > maximumBound) {
			return;
		}
		
		addOrGetId(schedule);
	}
	
	public void putAll(Collection<? extends TreeSchedule> schedules) {
		for(TreeSchedule s : schedules) {
			put(s);
		}
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
	
	int getLowerBound(int id) {
		if(id == -1) {
			return rootSchedule.getLowerBound();
		}
		
		ScheduleBlock block = blocks.get(id / blockSize);
		int subIndex = id % blockSize;
		return block.lowerBound[subIndex];
	}
	
	/** Adds a schedule to the array. If this schedule object was already in the array it is not re-added
	 * and the index is returned.
	 * 
	 * @throws OutOfMemoryError if there are too many schedules in this array.
	 * @return the id that was allocated to this object.
	 **/
	int addOrGetId(TreeSchedule schedule) throws OutOfMemoryError {
		if(schedule instanceof ArrayBackedSchedule) {
			return ((ArrayBackedSchedule) schedule).getIndex();
		}
		
		if(schedule.getParent() == null) {
			if(rootSchedule == null) {
				size++;
				rootSchedule = schedule; //Update root, this may be the first time.
				queue.put(-1);
			}
			
			return -1;
		}
		
		int widthBucket = getBucket(schedule);
		ScheduleBlock block = getNextBlock(widthBucket);

		int id = block.add(schedule);

		size++;

		if(schedule.getLowerBound() < widthIndexNotInQueue * granularity) {
			queue.put(id);
		}
		
		return id;
	}

	private int getBucket(TreeSchedule schedule) {
		return schedule.getLowerBound() / granularity;
	}

	private ScheduleBlock getNextBlock(int widthBucket) {
		while(widths.size() <= widthBucket) {
			widths.add(new ArrayList<>());
		}
		
		List<ScheduleBlock> width = widths.get(widthBucket);
		if(width.isEmpty()) {
			ScheduleBlock block = allocateNewBlock();
			width.add(block);
			return block;
		}
		
		ScheduleBlock block = width.get(width.size() - 1);
		if(block.isFull()) {
			block = allocateNewBlock();
			width.add(block);
		}
		
		return block;
	}

	private ScheduleBlock allocateNewBlock() {
		ScheduleBlock newBlock = new ScheduleBlock(blocks.size());
		blocks.add(newBlock);
		return newBlock;
	}

	/** Deletes all schedules with a bound greater than the maximum */
	public void pruneStorage(int maxBound) {
		int lastGoodWidth = maxBound / granularity;
		
		for(int w = lastGoodWidth + 1; w < widths.size(); w++) {
			for(ScheduleBlock block : widths.get(w)) {
				blocks.set(block.slot, null); //Drop the block
				size -= block.size;
			}
		}
	}
	
	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return "(" + size() + ")";
	}

	//Used for testing
	SchedulePriorityQueue getQueue() {
		return queue;
	}
}
