package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** This is an implementation of a priority queue, that holds indexs into the ScheduleArray. */
final class SchedulePriorityQueue implements Iterable<TreeSchedule> {
	/** This array stores all of the schedule */
	private final ScheduleArray scheduleArray;
	
	private int length;
	private int[] minHeap;
	
	/** Creates a priority queue. The backing array is used to query the lower bound of the schedule. */
	SchedulePriorityQueue(ScheduleArray backingArray) {
		scheduleArray = backingArray;
		length = 0;
		minHeap = new int[1024 * 1024];
	}

	/** Places an id into the queue. */
	void put(int id) {
		expandIfNeeded();
		minHeap[length++] = id;
		fixTail();
	}

	/** This is a debug method that is used to check if the heap is valid. It
	 * has O(N) runtime. */
	void checkHeapProperty() {
		checkHeapProperty(0);
	}
	
	private int checkHeapProperty(int i) {
		int left = leftChild(i);
		int right = rightChild(i);
		int bound = scheduleArray.getLowerBound(minHeap[i]);
		
		if(left >= length) {
			return bound;
		}
		
		int leftBound = checkHeapProperty(left);
		assert leftBound >= bound;
		
		if(right >= length) {
			return bound;
		}
		
		int rightBound = checkHeapProperty(right);
		assert rightBound >= bound;
		
		return bound;
	}
	
	private void expandIfNeeded() {
		if(length >= minHeap.length) {
			minHeap = Arrays.copyOf(minHeap, Math.max(minHeap.length * 2, 1024));
		}
	}
	
	/** Returns the number of elements stored in the heap. */
	int size() {
		return length;
	}

	/** Returns and removes the best item from this queue */
	int pop() {
		int index = minHeap[0];
		minHeap[0] = minHeap[--length];
		fixHead();
		
		return index;
	}

	/** Returns the best item from this queue */
	int peek() {
		return minHeap[0];
	}
	
	/** This is a debug method used to return an array of all schedules in this queue. */
	TreeSchedule[] toArray() {
		return IntStream.range(0, length).map(i -> minHeap[i]).mapToObj(scheduleArray::get).toArray(TreeSchedule[]::new);
	}
	
	private void fixTail() {
		if(length <= 1) {
			return;
		}
		
		int childBound = scheduleArray.getLowerBound(minHeap[length - 1]);
		for(int parent = length - 1; parent > 0; ) {
			int child = parent;
			parent = parent(parent);
			int parentBound = scheduleArray.getLowerBound(minHeap[parent]);
			
			if(isSmallerThan(child, parent, childBound, parentBound)) {
				swap(child, parent);
			} else {
				return; //If we did not swap, stop the fix
			}
		}
	}
	
	private boolean isSmallerThan(int a, int b, int aBound, int bBound) {
		if(aBound < bBound)
			return true;
		
		if(aBound > bBound)
			return false;
		
		int aDepth = scheduleArray.getNumberOfTasks(minHeap[a]);
		int bDepth = scheduleArray.getNumberOfTasks(minHeap[b]);
		
		return bDepth < aDepth;
	}

	private void swap(int a, int b) {
		int tmp = minHeap[a];
		minHeap[a] = minHeap[b];
		minHeap[b] = tmp;
	}

	private void fixHead() {
		//Swap with the next lowest element, and so on.
		int parent = 0;
		int parentBound = scheduleArray.getLowerBound(minHeap[parent]);
		
		boolean isDone = false;
		
		do {
			int leftChild = leftChild(parent);
			if(leftChild >= length) {
				return;
			}
			
			int rightChild = rightChild(parent);
			
			//Initially choose the left child
			int smallestChildBound = scheduleArray.getLowerBound(minHeap[leftChild]);
			int smallestChild = leftChild;
			if(rightChild < length) {
				int rightBound = scheduleArray.getLowerBound(minHeap[rightChild]);
				
				//There is a valid right child.
				if(isSmallerThan(rightChild, smallestChild, rightBound, smallestChildBound)) {
					smallestChildBound = rightBound;
					smallestChild = rightChild;
				}
			} else {
				isDone = true; //We still need to check the left child
			}
			
			//Check if a swap is needed
			if(isSmallerThan(smallestChild, parent, smallestChildBound, parentBound)) {
				swap(smallestChild, parent);
				
				parent = smallestChild;
			} else {
				isDone = true;
			}
		} while(!isDone);
	}

	private int parent(int index) {
		return (index - 1) / 2;
	}
	
	private int rightChild(int index) {
		return index * 2 + 2;
	}
	
	private int leftChild(int index) {
		return index * 2 + 1;
	}

	/** Iterates through all of the items in the priority queue in no particular order. 
	 * Results are undefined if the collection is edited while iterating. */
	@Override
	public Iterator<TreeSchedule> iterator() {
		return new Iterator<TreeSchedule>() {
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < length;
			}

			@Override
			public TreeSchedule next() {
				return scheduleArray.get(index++);
			}
		};
	}
	
	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}
}
