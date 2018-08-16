package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** This is a schedule array that stores the schedules in blocks that contain schedules with a similar minimum bound. These blocks
 * are retrieved one at a time and can also be dropped from memory if requested. */
final class BlockScheduleArray extends ScheduleArray {
	private final int granularity;
	private final List<List<ScheduleBlock>> widths = new ArrayList<>();
	
	/** The first width of lower bounds that has not been added to the queue. */
	private int widthAfterQueue = 0;
	
	/** Any schedule with a lower bound greater than this can be purged. */
	private int maximumBound = Integer.MAX_VALUE;
	
	BlockScheduleArray(int blockSize, int granularity) {
		super(blockSize);
		
		this.granularity = granularity;
	}
	
	@Override
	ScheduleBlock getBlockFor(TreeSchedule schedule) {
		int width = getWidth(schedule.getLowerBound());
		
		while(widths.size() <= width) {
			widths.add(new ArrayList<>());
		}
		
		List<ScheduleBlock> blocksInWidth = widths.get(width);
		
		if(blocksInWidth.size() == 0) {
			ScheduleBlock newBlock = allocateNewBlock();
			blocksInWidth.add(newBlock);
			
			return newBlock;
		} else {
			ScheduleBlock block = blocksInWidth.get(blocksInWidth.size() - 1);
			
			if(block.isFull()) {
				block = allocateNewBlock();
				blocksInWidth.add(block);
				
				return block;
			}
			
			return block;
		}
	}
	
	/** Prunes all solutions with a bound greater than or equal to max. */
	void setPruneMaximum(int max) {
		if(max >= maximumBound) {
			return;
		}
		
		int newLargestWidth = getWidth(max - 1);
		int oldLargestWidth = getWidth(maximumBound - 1);
		maximumBound = max;
		
		if(newLargestWidth < widthAfterQueue - 1) {
			//Don't prune the widths in the queue.
			newLargestWidth = widthAfterQueue - 1;
		}
		
		//Prune useless widths
		for(int i = newLargestWidth + 1; i < widths.size() && i <= oldLargestWidth; i++) {
			for(ScheduleBlock block : widths.get(i)) {
				block.remove();
			}
			
			widths.get(i).clear();
		}
	}
	
	int getPruneMaximum() {
		return maximumBound;
	}
	
	/** Returns false if there are no more widths to add */
	boolean addNextWidthTo(SchedulePriorityQueue queue) {
		if(widthAfterQueue == widths.size()) {
			return false;
		}
		
		List<ScheduleBlock> width = widths.get(widthAfterQueue++);
		
		for(ScheduleBlock block : width) {
			block.addToQueue(queue);
		}
		
		return true;
	}
	
	private int getWidth(int bound) {
		return bound / granularity;
	}

	int getEndOfQueue() {
		return widthAfterQueue * granularity;
	}
}
