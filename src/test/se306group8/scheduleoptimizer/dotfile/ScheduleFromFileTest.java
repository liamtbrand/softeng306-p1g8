package se306group8.scheduleoptimizer.dotfile;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ScheduleFromFileTest {
	@Test
	void testGetTotalRuntime() {
		assertEquals(10, TestScheduleUtils.createTestScheduleA().getTotalRuntime());
	}
}
