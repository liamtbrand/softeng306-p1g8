package se306group8.scheduleoptimizer;

/**
 * 
 * Represents a data dependency between two tasks, and a communication cost.
 *
 */
public class Dependency {
	
	private final Task child;
	private final Task parent;
	private final int cost;
	
	public Dependency( Task child, Task parent, int cost ) {
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
	
	// TODO rename this something nicer? It's sort of confusing.
	public int getRemoteCost() {
		return cost;
	}
	
}
