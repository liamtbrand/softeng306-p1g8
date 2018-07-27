package se306group8.scheduleoptimizer.taskgraph;

import java.util.Collection;
import java.util.HashSet;
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
		this.children = new HashSet<>(children);
	}
	
	void setParentDependencies(Collection<Dependency> parents){
		this.parents = new HashSet<>(parents);
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
	
	@Override
	public boolean equalsIgnoringParents(Task other) {
		return other.communicationCost == communicationCost && other.target.equalsIgnoringParents(target);
	}

	@Override
	public boolean equalsIgnoringChildren(Task other) {
		return other.communicationCost == communicationCost && other.target.equalsIgnoringChildren(source);
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Dependency)) {
			return false;
		}
		
		Dependency dep = (Dependency) other;
		
		return dep.communicationCost == communicationCost && dep.target.equalsIgnoringParents(target) && dep.source.equalsIgnoringChildren(source);
	}

	@Override
	public int hashCodeIgnoringParents() {
		return Objects.hash(communicationCost, target.hashCodeIgnoringParents());
	}

	@Override
	public int hashCodeIgnoringChildren() {
		return Objects.hash(communicationCost, source.hashCodeIgnoringChildren());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(communicationCost, source.hashCodeIgnoringChildren(), target.hashCodeIgnoringParents());
	}
}
