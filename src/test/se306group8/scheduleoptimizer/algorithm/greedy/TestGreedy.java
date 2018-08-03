package se306group8.scheduleoptimizer.algorithm.greedy;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TestScheduleUtils;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class TestGreedy {
	
	private void testGraph(TaskGraph graph) {
		Algorithm greedy = new GreedySchedulingAlgorithm();
		Schedule s1 = greedy.produceCompleteSchedule(graph, 2);
		TestScheduleUtils.checkValidity(s1,2);
	}
	
	@Test
	public void testGraphA() {
		TaskGraph tgA = TestGraphUtils.buildTestGraphA();
		testGraph(tgA);		
	}
	
	@Test
	public void testGraphB() {
		TaskGraph tgB = TestGraphUtils.buildTestGraphB();
		testGraph(tgB);		
	}
	
	@Test
	public void testGraphC() {
		TaskGraph tgC = TestGraphUtils.buildTestGraphC();
		testGraph(tgC);		
	}
}
