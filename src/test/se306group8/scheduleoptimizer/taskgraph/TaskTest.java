package se306group8.scheduleoptimizer.taskgraph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIsNotIndependant() {
		TaskGraph a = TestGraphUtils.buildTestGraphA();
		for(Task t : a.getAll()) {
			assertFalse(t.isIndependant());
		}
	}
	
	@Test
	void testIsIndependant() {
		TaskGraph b = TestGraphUtils.buildTestGraphB();
		for(Task t : b.getAll()) {
			assertTrue(t.isIndependant());
		}
	}
}
