package se306group8.scheduleoptimizer.algorithm.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

class SchedulePriorityQueueTest {
	private SchedulePriorityQueue priorityQueue;
	private ScheduleArray array;
	private TaskGraph graph;
	private Task task1, task2;
	private int parent;
	private int child1, child2, child3;
	
	@BeforeEach
	public void setUp() {
		graph = TestGraphUtils.buildTestGraphA();
		task1 = graph.getAll().get(0);
		task2 = graph.getAll().get(1);
		
		array = new ScheduleArray(100_000);
		
		parent = array.add(new TreeSchedule(graph, s -> code(s), 2));
		child1 = array.add(new TreeSchedule(task1, 1, array.get(parent)));
		child2 = array.add(new TreeSchedule(task1, 2, array.get(parent)));
		child3 = array.add(new TreeSchedule(task2, 1, array.get(child1)));
		
		priorityQueue = new SchedulePriorityQueue(array);
	}

	//Order [ parent, child2, child1, child3 ]
	private int code(TreeSchedule s) {
		if(s.isEmpty()) {
			return 0;
		} else if(s.getMostRecentAllocation().processor == 2) {
			return 1;
		} else if(s.getMostRecentAllocation().task.equals(task1)) {
			return 2;
		} else {
			return 3;
		}
	}

	@Test
	void testPutPoll() {
		priorityQueue.put(child1);
		priorityQueue.put(child2);
		priorityQueue.put(child3);
		priorityQueue.put(parent);
		
		priorityQueue.checkHeapProperty();
		assertEquals(parent, priorityQueue.peek());
		
		priorityQueue.checkHeapProperty();
		assertEquals(parent, priorityQueue.pop());
		
		priorityQueue.checkHeapProperty();
		priorityQueue.put(child3);
		
		priorityQueue.checkHeapProperty();
		assertEquals(child2, priorityQueue.pop());
		
		priorityQueue.checkHeapProperty();
		assertEquals(child1, priorityQueue.pop());
		
		priorityQueue.checkHeapProperty();
		assertEquals(child3, priorityQueue.pop());
	}
}
