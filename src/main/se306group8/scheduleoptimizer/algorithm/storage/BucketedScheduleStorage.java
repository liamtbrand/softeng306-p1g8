package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.Arrays;
import java.util.Collection;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** This storage stores a large number of schedules. 
 * The top bucket of schedules are stored in a priority queue. The rest are in
 * unsorted arrays that can be dropped as needed. */
public class BucketedScheduleStorage implements ScheduleStorage {
	private static final int MAX_NUMBER_OF_BUCKETS = 100;
	
	private SchedulePriorityQueue queue;
	private final ScheduleArray[] arrays;
	
	//These are the hard bounds, used to size the array
	private final int minimumBound;
	private final int maximumBound;
	
	//These are the running bounds on the solution
	private int upperBound;
	
	private int size = 0;
	private int indexOfFirstArray = 0;
	
	/** Creates a schedule storage for a given range of bounds.
	 * The bounds can be violated, but this will hurt performance.
	 * @param upperLimit The largest lower bound that should be entered into this array.
	 * @param lowerLimit The smallest lower bound that should be entered into this array.
	 **/
	public BucketedScheduleStorage(int upperLimit, int lowerLimit) {
		queue = new SchedulePriorityQueue();
		this.maximumBound = upperLimit;
		this.minimumBound = lowerLimit;
		
		int difference = maximumBound - minimumBound;
		
		int numberOfArrays = difference - 1;
		if(numberOfArrays > MAX_NUMBER_OF_BUCKETS) {
			numberOfArrays = MAX_NUMBER_OF_BUCKETS;
		} else if(numberOfArrays < 0) {
			numberOfArrays = 0;
		}
		
		arrays = new ScheduleArray[numberOfArrays];
		
		for(int i = 0; i < arrays.length; i++) {
			arrays[i] = new ScheduleArray();
		}
	}
	
	/** Stores a schedule in storage. */
	@Override
	public void storeSchedule(TreeSchedule schedule) {
		if(schedule.getLowerBound() > upperBound) {
			return;
		}
		
		size++;
		
		int bound = schedule.getLowerBound();
		int index = computeArrayIndex(bound);
		
		if(index == 0) {
			queue.put(schedule);
		} else {
			arrays[index - 1].addOrGetId(schedule);
		}
	}

	private int computeArrayIndex(int bound) {
		int index = bound / (arrays.length + 1);
		
		if(index < 0) {
			index = 0;
		} else if(index >= arrays.length + 1) {
			index = arrays.length;
		}
		return index;
	}
	
	/** Stores a list of schedules in storage. */
	@Override
	public void storeSchedules(Collection<TreeSchedule> schedules) {
		for(TreeSchedule partial : schedules) {
			storeSchedule(partial);
		}
	}
	
	/** Prunes all solutions that have a lower bound larger than this number. */
	@Override
	public void pruneSolutions(int maximum) {
		upperBound = maximum;
		
		int index = computeArrayIndex(maximum - minimumBound);
		
		//Prune all arrays above the queried array
		int pruneFrom = index + 1;
		
		for(int array = pruneFrom - 1; array < arrays.length; array++) {
			size -= arrays[array].size();
			arrays[array].clear();
		}
	}
	
	@Override
	public TreeSchedule getBestSchedule() {
		if(queue.size() != 0) {
			return queue.poll();
		} else {
			queue = new SchedulePriorityQueue(arrays[indexOfFirstArray]);
			return getBestSchedule();
		}
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public String toString() {
		return Arrays.deepToString(queue.toArray());
	}
}
