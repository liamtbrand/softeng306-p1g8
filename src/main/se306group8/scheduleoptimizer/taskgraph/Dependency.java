package se306group8.scheduleoptimizer.taskgraph;

/**
 * Represents a data dependency between two tasks, and a communication cost.
 */
public final class Dependency {
	private final Task child;
	private final Task parent;
	private final int cost;
	
	Dependency(Task child, Task parent, int cost) {
		this.child = child;
		this.parent = parent;
		this.cost = cost;
	}
	
	public Task getChild() {
		return child;
	}
	
	public Task getParent() {
		return parent;
	}
	
	public int getCommunicationCost() {
		return cost;
	}
	
}
