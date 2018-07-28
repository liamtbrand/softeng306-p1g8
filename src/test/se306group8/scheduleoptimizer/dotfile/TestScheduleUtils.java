package se306group8.scheduleoptimizer.dotfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		
		return new ScheduleFromFile(graph, lists);
	}
}
