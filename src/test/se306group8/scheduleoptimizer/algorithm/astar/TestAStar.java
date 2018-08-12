package se306group8.scheduleoptimizer.algorithm.astar;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.TestScheduleUtils;
import se306group8.scheduleoptimizer.algorithm.childfinder.BasicChildScheduleFinder;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class TestAStar {

	private void testGraph(TaskGraph graph) throws InterruptedException {
		// TODO improve these tests!
		Algorithm algorithm = new AStarSchedulingAlgorithm(new BasicChildScheduleFinder(1), h -> 0);
		Schedule s = algorithm.produceCompleteSchedule(graph, 1);
		TestScheduleUtils.checkValidity(s,1);
	}
	
	@Test
	public void testGraphA() throws InterruptedException {
		TaskGraph tgA = TestGraphUtils.buildTestGraphA();
		testGraph(tgA);
	}
	
	@Test
	public void testGraphB() throws InterruptedException {
		TaskGraph tgB = TestGraphUtils.buildTestGraphB();
		testGraph(tgB);
	}
	
	@Test
	public void testGraphC() throws InterruptedException {
		TaskGraph tgC = TestGraphUtils.buildTestGraphC();
		testGraph(tgC);
	}
	
}
