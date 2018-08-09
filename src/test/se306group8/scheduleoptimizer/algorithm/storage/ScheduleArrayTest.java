package se306group8.scheduleoptimizer.algorithm.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class ScheduleArrayTest {
	private ScheduleStorage array;
	private TaskGraph graph;
	private Task task;
	private TreeSchedule parent;
	private TreeSchedule child1;
	private TreeSchedule child2;
	
	@BeforeEach
	public void setUp() {
		graph = TestGraphUtils.buildTestGraphA();
		task = graph.getAll().get(0);
		
		int[] tmp = new int[] { 5 };
		
		parent = new TreeSchedule(graph, s -> 0xff & s.hashCode());
		child1 = new TreeSchedule(task, 1, parent);
		child2 = new TreeSchedule(task, 2, parent);
		
		array = new ScheduleStorage();
	}
	
	@Test
	public void testGetLowerBound() {
		int id = array.addOrGetId(parent);
		assertEquals(parent.getLowerBound(), array.getLowerBound(id));
		
		id = array.addOrGetId(child1);
		assertEquals(child1.getLowerBound(), array.getLowerBound(id));
		
		id = array.addOrGetId(child2);
		assertEquals(child2.getLowerBound(), array.getLowerBound(id));
	}

	@Test
	public void testGet() {
		int id = array.addOrGetId(child1);
		assertEquals(child1, array.get(id));

		id = array.addOrGetId(parent);
		assertEquals(parent, array.get(id));
		
		id = array.addOrGetId(child2);
		assertEquals(child2, array.get(id));
	}
	
	/** Tests the case where a new block is allocated */
	@Test 
	public void addNewBlock() {
		int lastId = 0;
		
		for(int i = 0; i < 5_000_000; i++) {
			lastId = array.addOrGetId(child1);
		}
		
		assertEquals(child1, array.get(lastId));
	}
}
