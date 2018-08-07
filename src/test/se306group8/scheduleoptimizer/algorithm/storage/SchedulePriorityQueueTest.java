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
	private SchedulePriorityQueue priorityQueue;
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
		child1 = new TreeSchedule(graph, task1, 1, parent);
		child2 = new TreeSchedule(graph, task2, 2, parent);
		child3 = new TreeSchedule(graph, task2, 1, child1);
		
		priorityQueue = new SchedulePriorityQueue();
	}

	//Order [ parent, child2, child1, child3 ]
	private int code(TreeSchedule s) {
		if(s.isEmpty()) {
			return 0;
		} else if(s.getMostRecentTask().equals(task1)) {
			return 2;
		} else if(s.getMostRecentProcessor() == 2) {
			return 1;
		} else {
			return 3;
		}
	}

	@Test
	void testPutPoll() {
		priorityQueue.putAll(Arrays.asList(parent, child1, child2));
		
		priorityQueue.checkHeapProperty();
		assertEquals(parent, priorityQueue.peek());
		assertEquals(3, priorityQueue.size());
		
		priorityQueue.checkHeapProperty();
		assertEquals(parent, priorityQueue.poll());
		assertEquals(2, priorityQueue.size());
		
		priorityQueue.checkHeapProperty();
		priorityQueue.put(child3);
		assertEquals(3, priorityQueue.size());
		
		priorityQueue.checkHeapProperty();
		assertEquals(child2, priorityQueue.poll());
		assertEquals(2, priorityQueue.size());
		
		priorityQueue.checkHeapProperty();
		assertEquals(child1, priorityQueue.poll());
		assertEquals(1, priorityQueue.size());
		
		priorityQueue.checkHeapProperty();
		assertEquals(child3, priorityQueue.poll());
		assertEquals(0, priorityQueue.size());
	}
}
