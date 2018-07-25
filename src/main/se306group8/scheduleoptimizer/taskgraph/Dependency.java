package se306group8.scheduleoptimizer.taskgraph;

/**
 * Represents a data dependency between two tasks, and a communication cost.
 */
public final class Dependency {
	private final Task source;
	private final Task target;
	private final int communicationCost;
	
	Dependency(Task source, Task target, int communicationCost) {
		this.source = source;
		this.target = target;
		this.communicationCost = communicationCost;
	}
	
	public Task getTarget() {
		return target;
	}
	
	public Task getSource() {
		return source;
	}
	
	public int getCommunicationCost() {
		return communicationCost;
	}
	
}
