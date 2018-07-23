package se306group8.scheduleoptimizer;
import java.util.Collection;
import java.util.Collections;

/**
 * 
 * Represents a single task in the DAG, with a weight, name and dependencies.
 *
 */
public class Task {
	
	private final String name;
	private final Collection< Dependency > children;
	private final Collection< Dependency > parents;
	private final int cost;
	
	public Task( String name, Collection< Dependency > children, Collection< Dependency > parents, int cost ) {
		this.name = name;
		this.children = children;
		this.parents = parents;
		this.cost = cost;
	}
	
	/**
	 * 
	 * @return Returns the name of this task.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return Returns a collection of tasks that depend on this task.
	 */
	public Collection< Dependency > getChildren() {
		return Collections.unmodifiableCollection( children );
	}
	
	public Collection< Dependency > getParents() {
		return Collections.unmodifiableCollection( parents );
	}
	
	/**
	 * 
	 * @return Returns the time units this task requires.
	 */
	public int getCost() {
		return cost;
	}
	
}
