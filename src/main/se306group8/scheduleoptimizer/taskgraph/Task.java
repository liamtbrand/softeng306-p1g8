package se306group8.scheduleoptimizer.taskgraph;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Represents a single task in a task graph, with a weight, name and dependencies.
 */
public final class Task implements GraphEquality<Task> {
	private final String name;
	private Collection<Dependency> children;
	private Collection<Dependency> parents;
	private final int cost;
	
	Task(String name, int cost){
		this.name = name;
		this.cost = cost;
	}
	
	void setChildDependencies(Collection<Dependency> children){
		this.children = Collections.unmodifiableCollection(children);
	}
	
	void setParentDependencies(Collection<Dependency> parents){
		this.parents = Collections.unmodifiableCollection(parents);
	}
	
	/** 
	 * Returns the name of this task. 
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
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
	
	@Override
	public boolean equalsIgnoringParents(Task other) {
		if(other == this)
			return true;
		
		return other.cost == cost && GraphEqualityUtils.setsEqualIgnoringParents(children, other.children);
	}

	@Override
	public boolean equalsIgnoringChildren(Task other) {
		if(other == this)
			return true;
		
		return other.cost == cost && GraphEqualityUtils.setsEqualIgnoringChildren(parents, other.parents);
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this)
			return true;
		
		if(!(other instanceof Task)) {
			return false;
		}
		
		Task task = (Task) other;
		
		return cost == task.cost && GraphEqualityUtils.setsEqualIgnoringChildren(parents, task.parents) && GraphEqualityUtils.setsEqualIgnoringParents(children, task.children);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cost, name, children.size(), parents.size());
	}
}
