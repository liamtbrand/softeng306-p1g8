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
}
