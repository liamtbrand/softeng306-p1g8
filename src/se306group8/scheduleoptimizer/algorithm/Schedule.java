package se306group8.scheduleoptimizer.algorithm;

import java.util.List;
import java.util.ListIterator;

import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Represents an allocation of tasks to different processors in a specific order. 
 * 
 * This is the result object that is returned from the algorithm.
 */
public final class Schedule implements Iterable<List<Task>> {
	private final TaskGraph graph;
	private final List<List<Task>> taskLists;
	
	Schedule(TaskGraph graph, List<List<Task>> taskLists) {
		assert graph != null && taskLists != null;
		
		this.graph = graph;
		this.taskLists = taskLists;
	}
	
	public TaskGraph getGraph() {
		return graph;
	}
	
	public int getNumberOfUsedProcessors() {
		return taskLists.size();
	}
	
	public List<Task> getTasksOnProcessor(int processor) {
		return taskLists.get(processor);
	}

	@Override
	public ListIterator<List<Task>> iterator() {
		return taskLists.listIterator();
	}
	
	public int computeStartTime(Task task) {
		
	}
	
	public int computeTotalRuntime() {
		
	}
}
