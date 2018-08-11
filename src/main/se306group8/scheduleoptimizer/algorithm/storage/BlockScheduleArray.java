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
			ScheduleBlock newBlock = super.getBlockFor(schedule);
			blocksInWidth.add(newBlock);
			
			return newBlock;
		} else {
			ScheduleBlock block = blocksInWidth.get(blocksInWidth.size() - 1);
			
			if(block.isFull()) {
				block = super.getBlockFor(schedule);
				blocksInWidth.add(block);
				
				return block;
			}
			
			return block;
		}
	}
	
	/** Prunes all solutions with a bound greater than max */
	void setPruneMaximum(int max) {
		if(max >= maximumBound) {
			return;
		}
		
		int newLargestWidth = getWidth(max);
		int oldLargestWidth = getWidth(maximumBound);
		
		assert newLargestWidth >= widthAfterQueue - 1 : "The width in the queue should not be pruned.";
		
		maximumBound = max;
		
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
	
	void addNextWidthTo(SchedulePriorityQueue queue) {
		List<ScheduleBlock> width = widths.get(widthAfterQueue++);
		
		for(ScheduleBlock block : width) {
			int start = block.slot * blockSize;
			int end = start + block.size;
			
			for(int i = start; i < end; i++) {
				queue.put(i);
			}
		}
	}
	
	private int getWidth(int bound) {
		return bound / granularity;
	}

	int getEndOfQueue() {
		return widthAfterQueue * granularity;
	}
}
