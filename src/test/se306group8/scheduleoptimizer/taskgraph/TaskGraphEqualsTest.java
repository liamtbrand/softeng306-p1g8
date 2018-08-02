package se306group8.scheduleoptimizer.taskgraph;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskGraphEqualsTest {

	@Test
	void testEqualsTaskGraph() {
		Assertions.assertEquals(TestGraphUtils.buildTestGraphA(), TestGraphUtils.buildTestGraphA());
	}

	@Test
	void testEqualsTask() {
		Task b1 = null;
		for(Task t : TestGraphUtils.buildTestGraphA().getAll()) {
			if(t.getName().equals("b")) {
				b1 = t;
			}
		}
		
		Task b2 = null;
		for(Task t : TestGraphUtils.buildTestGraphA().getAll()) {
			if(t.getName().equals("b")) {
				b2 = t;
			}
		}
		
		Task a1 = null;
		for(Task t : TestGraphUtils.buildTestGraphA().getAll()) {
			if(t.getName().equals("a")) {
				a1 = t;
			}
		}
		
		Assertions.assertEquals(b1, b2);
		Assertions.assertNotEquals(a1, b1);
	}
	
	/** Checks for two tasks with the cost, and dependencies but differing names. */
	@Test
	void testNotEquals() {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		builder.addTask("a", 1);
		builder.addTask("b", 1);
		
		TaskGraph graph = builder.buildGraph();
		
		for(Task t1 : graph.getAll()) {
			for(Task t2 : graph.getAll()) {
				Assertions.assertEquals(t1.equals(t2), t1 == t2);
			}
		}
	}
	
	/** Check equality of two tasks with different costs, dependencies and same names */
	@Test
	void testNotEqualsCosts() {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		builder.addTask("a", 1);
		builder.addTask("a", 2);
		
		TaskGraph graph = builder.buildGraph();
		
		for(Task t1 : graph.getAll()) {
			for(Task t2 : graph.getAll()) {
				Assertions.assertEquals(t1.equals(t2), t1 == t2);
			}
		}
	}
	
	/** Check the communication cost of two nodes known to be equal, is rendered equal */
	@Test
	void testCommunicationCostsEqual() {
		Task a = null;
		Task b = null;
		Task c = null;
		Task d = null;
		
		// Assign task objects as specified in Tester graph
		for(Task t : TestGraphUtils.buildTestGraphA().getAll()) {
			switch(t.getName()) {
				case "a":
					a = t;
					break;
				case "b":
					b = t;
					break;
				case "c":
					c = t;
					break;
				case "d":
					d = t;
					break;
			}
		}	
		Assertions.assertEquals(a.getCost(), d.getCost());
		Assertions.assertEquals(b.getCost(), c.getCost());
	}
	
	
}
