package se306group8.scheduleoptimizer.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class TestScheduleUtils {
	public static Schedule createTestScheduleA() {
		TaskGraph graph = TestGraphUtils.buildTestGraphA();
		
		//Topological Order
		List<Task> tasks = new ArrayList<>(graph.getAll());
		tasks.sort((a, b) -> a.getName().compareTo(b.getName()));
		
		List<List<Task>> lists = new ArrayList<>();
		
		for(Task t : tasks) {
			lists.add(Arrays.asList(t));
		}
		
		return new ListSchedule(graph, lists);
	}
	
	public static void checkValidity(Schedule schedule) {
		TaskGraph graph = schedule.getGraph();
		
		int[] processorStart = new int[schedule.getNumberOfUsedProcessors()];
		int start = 0;
		
		for(Task task : graph.getAll()) {
			start = processorStart[schedule.getProcessorNumber(task) - 1];
			
			processorStart[schedule.getProcessorNumber(task) - 1] = schedule.getStartTime(task) + task.getCost();
			
			for(Dependency dep : task.getParents()) {
				int comCost = (schedule.getProcessorNumber(task) == schedule.getProcessorNumber(dep.getSource())) ? 0 : dep.getCommunicationCost();
				start = Math.max(start, comCost + dep.getSource().getCost() + schedule.getStartTime(dep.getSource()));
			}
			
			assertEquals(start, schedule.getStartTime(task));
		}
	}
}
