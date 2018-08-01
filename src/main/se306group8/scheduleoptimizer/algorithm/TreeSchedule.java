package se306group8.scheduleoptimizer.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/** This is class that is used to wrap the very compressed representation that is used for ScheduleStorage. It is
 * not intended to be used for the storage itself, as the fact that it is an object instantly requires that it takes up
 * 16B, and usually 24B. Using clever compression we can store each schedule in 10B, doubling the number of solutions we can
 * store. */
public class TreeSchedule implements Comparable<TreeSchedule> {
	
	//Constructed fields
	private final TreeSchedule parent;
	private final Task task;
	private final int processor, lowerBound;
	private final TaskGraph graph;
	
	//Calculated fields
	
	public TreeSchedule(TaskGraph graph, TreeSchedule parent, Task task, int processor, int lowerBound) {
		this.graph = graph;
		this.parent = parent;
		this.task = task;
		this.processor = processor;
		this.lowerBound = lowerBound;
	}
	
	public List<List<Task>> computeTaskLists() {
		List<List<Task>> result;
		if(parent == null) {
			result = new ArrayList<>();
		} else {
			result = parent.computeTaskLists();
		}
		
		while(result.size() < processor) {
			result.add(new ArrayList<>());
		}
		
		result.get(processor).add(task);
		
		return result;
	}
	
	/** Returns the full schedule. This may be null if this solution is not a full solution. */
	public Schedule getFullSchedule() {
		return new ListSchedule(graph, computeTaskLists());
	}
	
	public Task getMostRecentTask() {
		return task;
	}
	
	public int getMostRecentProcessor() {
		return processor;
	}

	public TreeSchedule getParent() {
		return parent;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	@Override
	public int compareTo(TreeSchedule o) {
		return lowerBound - o.lowerBound;
	}

	public TaskGraph getGraph() {
		return this.graph;
	}
}
