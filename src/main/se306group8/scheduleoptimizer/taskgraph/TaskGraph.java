package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * TaskGraph is an immutable representation of the DAG, including the weight annotations.
 *
 */
public final class TaskGraph {
	private final List<Task> topologicalOrder;
	private final Set<Task> roots;
	private final Set<Dependency> edges;
	private final String name;
	
	TaskGraph(String name, Collection<Task> tasks) {
		assert tasks.stream().noneMatch(Objects::isNull);
		
		topologicalOrder = new ArrayList<>();
		roots = new HashSet<>();
		this.name = name;
		
		for(Task task : tasks) {
			addTask(task);
			if(task.getParents().isEmpty()) {
				roots.add(task);
			}
		}
		
		edges = tasks.stream()
				.flatMap(t -> t.getChildren().stream())
				.collect(Collectors.toSet());
	}
	
	private void addTask(Task parent) {
		assert parent != null;
		
		for(Dependency dep : parent.getParents()) {
			Task task = dep.getSource();
			if(!topologicalOrder.contains(task)) {
				addTask(task);
			}
		}
		
		topologicalOrder.add(parent);
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
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof TaskGraph)) {
			return false;
		}
		
		TaskGraph otherGraph = (TaskGraph) other;
		
		//Compare two graphs by comparing the tasks
		return name.equals(otherGraph.name) && roots.equals(otherGraph.roots);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, roots);
	}
}
