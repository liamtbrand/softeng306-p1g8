package se306group8.scheduleoptimizer.algorithm.childfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Simple implementation of ChildScheduleFinder, this implementation does not do anything
 * smart to reduce the number of equivalent schedules.
 */
public class BasicChildScheduleFinder implements ChildScheduleFinder {
	
	private final int _processors;
	
	public BasicChildScheduleFinder(int processors) {
		_processors = processors;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TreeSchedule> getChildSchedules(TreeSchedule schedule) {
		
		TaskGraph graph = schedule.getGraph();

		Collection<Task> nextTasks = schedule.getAllocatable();
		
		// Get an array of integers that represents processor numbers
		int[] processors = IntStream.rangeClosed(1, _processors).toArray();
		
		List<TreeSchedule> childSchedules = new ArrayList<TreeSchedule>();
		
		for (Task task : nextTasks) {
			for (int processor : processors) {
				childSchedules.add(new TreeSchedule(graph, task, processor, schedule));
			}
		}
		return childSchedules;
	}
}
