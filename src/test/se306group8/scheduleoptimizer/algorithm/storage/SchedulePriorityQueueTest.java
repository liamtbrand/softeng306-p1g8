package se306group8.scheduleoptimizer.algorithm.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

class SchedulePriorityQueueTest {
	private ScheduleStorage priorityQueue;
	private TaskGraph graph;
	private Task task1, task2;
	private TreeSchedule parent;
	private TreeSchedule child1, child2, child3;
	
	@BeforeEach
	public void setUp() {
		graph = TestGraphUtils.buildTestGraphA();
		task1 = graph.getAll().get(0);
		task2 = graph.getAll().get(1);
		
		parent = new TreeSchedule(graph, s -> code(s));
		child1 = new TreeSchedule(task1, 1, parent);
		child2 = new TreeSchedule(task1, 2, parent);
		child3 = new TreeSchedule(task2, 1, child1);
		
		priorityQueue = new ScheduleStorage(1_000_000, 100_000);
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
		priorityQueue.putAll(Arrays.asList(parent, child1, child2));
		
		priorityQueue.getQueue().checkHeapProperty();
		assertEquals(parent, priorityQueue.peek());
		
		priorityQueue.getQueue().checkHeapProperty();
		assertEquals(parent, priorityQueue.pop());
		
		priorityQueue.getQueue().checkHeapProperty();
		priorityQueue.put(child3);
		
		priorityQueue.getQueue().checkHeapProperty();
		assertEquals(child2, priorityQueue.pop());
		
		priorityQueue.getQueue().checkHeapProperty();
		assertEquals(child1, priorityQueue.pop());
		priorityQueue.pop(); //There is another c1 added due to parent relationships
		
		priorityQueue.getQueue().checkHeapProperty();
		assertEquals(child3, priorityQueue.pop());
	}
}
