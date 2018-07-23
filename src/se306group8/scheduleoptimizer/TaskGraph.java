package se306group8.scheduleoptimizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * TaskGraph is an immutable representation of the DAG, including the weight annotations.
 *
 */
public class TaskGraph {
	
	private List< Task > tasks;
	private Collection< Task > roots;
	
	public TaskGraph( Collection< Task > tasks ) {
		
		// TODO compute topological ordering of tasks.
		this.tasks = new ArrayList< Task >();
		
		// TODO compute roots.
		this.roots = new ArrayList< Task >();
	}
	
	/**
	 * 
	 * @return Returns a List containing a topological ordering of all tasks.
	 */
	public List< Task > getAll() {
		return Collections.unmodifiableList( tasks );
	}
	
	/**
	 * 
	 * @return Returns a Collection of all tasks with no dependents.
	 */
	public Collection< Task > getRoots() {
		return Collections.unmodifiableCollection( roots );
	}
	
}
