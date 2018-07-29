package se306group8.scheduleoptimizer.algorithm.greedy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;


import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.dotfile.ScheduleFromFile;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class TestGreedy {
	
	private void testGraph(TaskGraph graph) {
		Algorithm greedy = new GreedySchedulingAlgorithm();
		
		Schedule s1 = greedy.produceCompleteSchedule(graph, 2);
		List<List<Task>> processorAllocation = new ArrayList<List<Task>>(2);
		processorAllocation.add(s1.getTasksOnProcessor(1));
		processorAllocation.add(s1.getTasksOnProcessor(2));
		
		ScheduleFromFile s2 = new ScheduleFromFile(graph, processorAllocation);
		
		assertEquals(s1.getNumberOfUsedProcessors(),s2.getNumberOfUsedProcessors());
		assertEquals(s1.getTotalRuntime(),s2.getTotalRuntime());
		
		List<Task> tasks= graph.getAll();
		for (Task t:tasks) {
			assertEquals(s1.getStartTime(t),s2.getStartTime(t));
			assertEquals(s1.getProcessorNumber(t),s2.getProcessorNumber(t));
		}
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
