package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 
 * TaskGraph is an immutable representation of the DAG, including the weight annotations.
 *
 */
public final class TaskGraph {
	private final List<Task> topologicalOrder;
	private final Collection<Task> roots;
	private final Collection<Dependency> edges;
	private final String name;
	private final Map<Task, Integer> bottomTime = new HashMap<>();
	
	TaskGraph(String name, Collection<Task> tasks) {
		assert tasks.stream().noneMatch(Objects::isNull);
		
		topologicalOrder = new ArrayList<>();
		roots = new ArrayList<>();
		this.name = name;
		
		for(Task task : tasks) {
			addTask(task);
			if(task.getParents().isEmpty()) {
				roots.add(task);
			}
		}
		
		edges = tasks.stream()
				.flatMap(t -> t.getChildren().stream())
				.collect(Collectors.toList());
		
		//Iterate backwards calculating the bottom times
		for(int i = topologicalOrder.size() - 1; i >= 0; i--) {
			Task task = topologicalOrder.get(i);
			int taskBottomTime = task.getCost();
			
			for(Dependency dep : task.getChildren()) {
				Task child = dep.getTarget();
				taskBottomTime = Math.max(taskBottomTime, bottomTime.get(child) + task.getCost());
			}
			
			bottomTime.put(task, taskBottomTime);
		}
	}
	
	private void addTask(Task parent) {
		assert parent != null;
		
		for(Dependency dep : parent.getParents()) {
			Task task = dep.getSource();
			if(!topologicalOrder.contains(task)) {
				addTask(task);
			}
		}
		
		if(!topologicalOrder.contains(parent)) {
			topologicalOrder.add(parent);
		}
	}
	
	/** Gets the task with the given id. */
	public Task getTask(int id) {
		return null; //TODO Robert fill this in when you commit your code.
	}
	
	/**
	 * Returns a List containing a topological ordering of all tasks.
	 */
	public List<Task> getAll() {
		return topologicalOrder;
	}
	
	/**
	 * Returns a Collection of all tasks with no dependents.
	 */
	public Collection<Task> getRoots() {
		return roots;
	}

	public Collection<Dependency> getEdges() {
		return edges;
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets the time from the start of this task to the end of the farthest descendant
	 */
	public int getBottomTime(Task task) {
		return bottomTime.get(task);
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this)
			return true;
		
		if(!(other instanceof TaskGraph)) {
			return false;
		}
		
		TaskGraph otherGraph = (TaskGraph) other;
		
		//Compare two graphs by comparing the tasks
		return name.equals(otherGraph.name) && GraphEqualityUtils.setsEqualIgnoringParents(roots, otherGraph.roots);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, topologicalOrder.size(), edges.size());
	}
}
