package se306group8.scheduleoptimizer.taskgraph;

import java.util.Collection;
import java.util.List;

/**
 * 
 * TaskGraph is an immutable representation of the DAG, including the weight annotations.
 *
 */
public class TaskGraph {
	
	//This could be replaced with a builder
	public TaskGraph(Collection<Task> tasks) {
		
	}
	
	/**
	 * Returns a List containing a topological ordering of all tasks.
	 */
	public List<Task> getAll() {
		
	}
	
	/**
	 * Returns a Collection of all tasks with no dependents.
	 */
	public Collection<Task> getRoots() {
		
	}
	
}
