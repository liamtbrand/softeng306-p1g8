package se306group8.scheduleoptimizer.dotfile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import se306group8.scheduleoptimizer.dotfile.ScheduleFromFile;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class TestScheduleUtils {
	public static Schedule createTestScheduleA() {
		TaskGraph graph = TestGraphUtils.buildTestGraphA();
		
		int numberOfProcessors = 2;
		List<List<Task>> lists = Stream.generate(ArrayList<Task>::new).limit(numberOfProcessors).collect(Collectors.toList());
		
		int i = 0;
		//Topological Order
		for(Task t : graph.getAll()) {
			lists.get(i % numberOfProcessors).add(t);
			i++;
		}
		
		return new ScheduleFromFile(graph, lists);
	}
}
