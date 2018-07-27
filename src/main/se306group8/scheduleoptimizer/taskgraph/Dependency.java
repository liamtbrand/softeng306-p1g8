package se306group8.scheduleoptimizer.taskgraph;

import java.util.Objects;

/**
 * Represents a data dependency between two tasks, and a communication cost.
 */
public final class Dependency implements GraphEquality<Dependency> {
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
	
	@Override
	public String toString() {
		return String.format("%s -> %s", source.toString(), target.toString());
	}

	@Override
	public boolean equalsIgnoringParents(Dependency other) {
		return other.communicationCost == communicationCost && other.target.equalsIgnoringParents(target);
	}

	@Override
	public boolean equalsIgnoringChildren(Dependency other) {
		return other.communicationCost == communicationCost && other.source.equalsIgnoringChildren(source);
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
	public int hashCode() {
		return Objects.hash(communicationCost, source.getName(), target.getName());
	}
}
