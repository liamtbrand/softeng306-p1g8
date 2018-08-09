package se306group8.scheduleoptimizer.algorithm.storage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.IntStream;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

/** This is an implementation of a priority queue, that holds indexs into the ScheduleArray. */
final class SchedulePriorityQueue implements Iterable<TreeSchedule> {
	/** This array stores all of the schedule */
	private final ScheduleArray scheduleArray;
	
	private int length;
	private int[] minHeap;
	
	SchedulePriorityQueue() {
		this.scheduleArray = new ScheduleArray();
		length = 0;
		minHeap = new int[1024 * 1024]; //Go big or go home :D
	}
	
	SchedulePriorityQueue(ScheduleArray backingArray) {
		scheduleArray = backingArray;
		length = backingArray.size();
		minHeap = new int[length];
		
		for(int i = 0; i < length; i++) {
			minHeap[i] = i;
		}
	}

	void put(TreeSchedule schedule) {
		int id = scheduleArray.addOrGetId(schedule);
		
		expandIfNeeded();
		minHeap[length++] = id;
		fixTail();
//		checkHeapProperty();
	}

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
		if(length == minHeap.length) {
			minHeap = Arrays.copyOf(minHeap, minHeap.length * 2);
		}
	}

	void putAll(Collection<? extends TreeSchedule> c) {
		for(TreeSchedule s : c) {
			put(s);
		}
	}
	
	int size() {
		return length;
	}

	/** Returns and removes the best item from this queue */
	TreeSchedule poll() {
		int index = minHeap[0];
		minHeap[0] = minHeap[--length];
		fixHead();
//		checkHeapProperty();
		
		return scheduleArray.get(index);
	}

	/** Returns the best item from this queue */
	TreeSchedule peek() {
		int index = minHeap[0];
		return scheduleArray.get(index);
	}
	
	TreeSchedule[] toArray() {
		return IntStream.range(0, length).map(i -> minHeap[i]).mapToObj(scheduleArray::get).toArray(TreeSchedule[]::new);
	}
	
	private void fixTail() {
		int childBound = scheduleArray.getLowerBound(minHeap[length - 1]);
		for(int parent = length - 1; parent > 0; ) {
			int child = parent;
			parent = parent(parent);
			int parentBound = scheduleArray.getLowerBound(minHeap[parent]);
			
			if(childBound < parentBound) {
				swap(child, parent);
			} else {
				return; //If we did not swap, stop the fix
			}
		}
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
				if(rightBound < smallestChildBound) {
					smallestChildBound = rightBound;
					smallestChild = rightChild;
				}
			} else {
				isDone = true; //We still need to check the left child
			}
			
			//Check if a swap is needed
			if(parentBound > smallestChildBound) {
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
}
