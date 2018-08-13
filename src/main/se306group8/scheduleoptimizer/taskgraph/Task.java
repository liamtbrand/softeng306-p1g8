package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single task in a task graph, with a weight, name and dependencies.
 */
public final class Task implements GraphEquality<Task> {
	private final String name;
	private List<Dependency> children;
	private List<Dependency> parents;
	private final int cost;
	private final int id;
	private boolean[] isParent;
	private boolean[] isChild;
	private boolean isIndependant = true;
	
	Task(String name, int cost, int id){
		this.name = name;
		this.cost = cost;
		this.id = id;
	}
	
	void setChildDependencies(Collection<Dependency> children){
		if(children.size() != 0) {
			isIndependant = false;
		}
		
		this.children = new ArrayList<>(children);
		
		int largestChild = 0;
		for(Dependency dep : children) {
			largestChild = Math.max(largestChild, dep.getTarget().getId());
		}
		
		isChild = new boolean[largestChild + 1];
		
		for(Dependency dep : children) {
			isChild[dep.getTarget().getId()] = true;
		}
	}
	
	void setParentDependencies(Collection<Dependency> parents){
		if(parents.size() != 0) {
			isIndependant = false;
		}
		
		this.parents = new ArrayList<>(parents);
		
		int largestParent = 0;
		for(Dependency dep : parents) {
			largestParent = Math.max(largestParent, dep.getSource().getId());
		}
		
		isParent = new boolean[largestParent + 1];
		
		for(Dependency dep : parents) {
			isParent[dep.getSource().getId()] = true;
		}
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
	
	public int getId() {
		return id;
	}
	
	/** 
	 * Returns all of the tasks that depend on this task. 
	 */
	public List<Dependency> getChildren() {
		return children;
	}
	
	/** 
	 * Returns all of the tasks that this task depends on. 
	 */
	public List<Dependency> getParents() {
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
		
		return other.name.equals(name) && other.cost == cost && GraphEqualityUtils.setsEqualIgnoringParents(children, other.children);
	}

	@Override
	public boolean equalsIgnoringChildren(Task other) {
		if(other == this)
			return true;
		
		return other.name.equals(name) && other.cost == cost && GraphEqualityUtils.setsEqualIgnoringChildren(parents, other.parents);
	}
	
	public boolean isIndependant() {
		return isIndependant;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this)
			return true;
		
		if(!(other instanceof Task)) {
			return false;
		}
		
		Task task = (Task) other;
		
		// TODO is this really equality? - Name should be unique, so do we need these other checks?
		
		return task.name.equals(name) && cost == task.cost && GraphEqualityUtils.setsEqualIgnoringChildren(parents, task.parents) && GraphEqualityUtils.setsEqualIgnoringParents(children, task.children);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cost, name, children.size(), parents.size());
	}

	public boolean isChild(Task task) {
		return task.getId() >= isChild.length ? false : isChild[task.getId()];
	}
	
	public boolean isParent(Task task) {
		return task.getId() >= isParent.length ? false : isParent[task.getId()];
	}
}
