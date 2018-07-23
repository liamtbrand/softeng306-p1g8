package se306group8.scheduleoptimizer.taskgraph;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a single task in a task graph, with a weight, name and dependencies.
 */
public class Task {
	private final String name;
	private Collection<Dependency> children;
	private Collection<Dependency> parents;
	private final int cost;
	
	//TODO limit the construction of tasks only to the taskgraph package
	public Task(String name, Collection<Dependency> children, Collection<Dependency> parents, int cost ) {
		this.name = name;
		this.children = Collections.unmodifiableCollection(children);
		this.parents = Collections.unmodifiableCollection(parents);
		this.cost = cost;
	}
	
	Task(String name, int cost){
		this.name = name;
		this.cost = cost;
	}
	
	void setChildDependencies(Collection<Dependency> children){
		this.children = Collections.unmodifiableCollection(children);
	}
	
	void setParentDependencies(Collection<Dependency> children){
		this.children = Collections.unmodifiableCollection(children);
	}
	
	/** 
	 * Returns the name of this task. 
	 */
	public String getName() {
		return name;
	}
	
	/** 
	 * Returns all of the tasks that depend on this task. 
	 */
	public Collection<Dependency> getChildren() {
		return children;
	}
	
	/** 
	 * Returns all of the tasks that this task depends on. 
	 */
	public Collection<Dependency> getParents() {
		return parents;
	}
	
	/** 
	 * Returns the time units this task requires. 
	 */
	public int getCost() {
		return cost;
	}
	
}
