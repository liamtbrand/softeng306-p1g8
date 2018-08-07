package se306group8.scheduleoptimizer.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import se306group8.scheduleoptimizer.algorithm.ListSchedule.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/** This class represents a partial schedule. It stores a parent schedule, and the task and processor that was added to it.
 * This allows a nice easy to work with model of a schedule. */
public class TreeSchedule implements Comparable<TreeSchedule> {
	
	//Constructed fields
	private final TreeSchedule parent;
	
	/** If this is null then this is the empty schedule */
	private final Task task;
	private final int processor;
	private final TaskGraph graph;
	private final MinimumHeuristic heuristic;
	
	//Calculated fields
	private int startTime;
	private int endTime;
	/** A bound that any full solution using this solution must be greater than. */
	private int lowerBound;
	private int idleTime;
	
	/** Creates an empty schedule.
	 * 
	 * @param heuristic The heuristic that is used to calculate the lower bound for this schedule and all children. */
	public TreeSchedule(TaskGraph graph, MinimumHeuristic heuristic) {
		assert graph != null;
		
		this.graph = graph;
		this.task = null;
		this.processor = 0;
		this.parent = null;
		this.heuristic = heuristic;
		
		startTime = 0;
		endTime = 0;
		lowerBound = heuristic.estimate(this);
		idleTime = 0;
	}
	
	/**
	 * Creates a schedule from a parent schedule and an allocation.
	 */
	public TreeSchedule(TaskGraph graph, Task task, int processor, TreeSchedule parent) {
		assert graph != null;
		
		this.graph = graph;
		this.task = task;
		this.processor = processor;
		this.parent = parent;
		this.heuristic = parent.heuristic;
		
		calculateFields();
	}
	
	private void calculateFields() {
		//Iterate backward through the tree. This first mention of this processor is the processor start time.
		//Also look for the mention of each child start.
		
		Map<Task, Dependency> parents = new HashMap<>();
		
		idleTime = parent.idleTime;
		
		startTime = 0;
		int endOfPreviousTask = 0;
		
		for(Dependency link : task.getParents()) {
			parents.put(link.getSource(), link);
		}
		
		for(TreeSchedule s = parent; !s.isEmpty(); s = s.parent) {
			if(s.processor == processor) {
				//Only set it if this is the first mention of that processor
				if(endOfPreviousTask == 0) {
					if(s.endTime > startTime) {
						startTime = s.endTime;
					}
					
					endOfPreviousTask = s.endTime;
				}
			} else {
				//Set the start time based on children
				Dependency dep = parents.get(s.task);
				if(dep != null) {
					int dataReadyTime = s.endTime + dep.getCommunicationCost();
					if(dataReadyTime > startTime) {
						startTime = dataReadyTime;
					}
				}
			}
		}
		
		endTime = startTime + task.getCost();
		idleTime += startTime - endOfPreviousTask;
	
		//This must be last as the other fields may be used by this calculation
		lowerBound = heuristic.estimate(this);
	}
	
	/** Returns true if the schedule is empty */
	public boolean isEmpty() {
		return task == null;
	}
	
	/** Returns the amount of time wasted */
	public int getIdleTime() {
		return idleTime;
	}
	
	/** Returns the ProcessorAllocation instance that this task was scheduled on.
	 * This returns null if the task has not been scheduled. */
	public ProcessorAllocation getAlloctionFor(Task t) {
		for(TreeSchedule s = this; !s.isEmpty(); s = s.parent) {
			if(s.task.equals(t)) {
				return new ProcessorAllocation(s.startTime, s.endTime, s.processor);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the last processor allocation on a processor null if no allocation
	 * @param processor
	 * @return
	 */
	public ProcessorAllocation getLastAllocationForProcessor(int processor) {
		for(TreeSchedule s = this; !s.isEmpty(); s = s.parent) {
			if(s.processor == processor) {
				return new ProcessorAllocation(s.startTime, s.endTime, s.processor);
			}
		}
		return null;
	}
	
	public List<List<Task>> computeTaskLists() {
		if(isEmpty()) {
			return new ArrayList<>();
		}
		
		List<List<Task>> result;
		if(parent.isEmpty()) {
			result = new ArrayList<>();
		} else {
			result = parent.computeTaskLists();
		}
		
		while(result.size() < processor) {
			result.add(new ArrayList<>());
		}
		
		result.get(processor - 1).add(task);
		
		return result;
	}
	
	public boolean isComplete() {
		int tasks = 0;
		for(TreeSchedule s = this; !s.isEmpty(); s = s.parent) {
			tasks++;
		}
		
		return tasks == graph.getAll().size();
	}
	
	/** Returns the full schedule. This may be null if this solution is not a full solution. */
	public Schedule getFullSchedule() {
		if(!isComplete())
			return null;
		
		return new ListSchedule(graph, computeTaskLists());
	}
	
	public Task getMostRecentTask() {
		return task;
	}
	
	public int getMostRecentProcessor() {
		return processor;
	}

	public TreeSchedule getParent() {
		return parent;
	}
	
	public int getLowerBound() {
		return lowerBound;
	}

	public String toString() {
		return computeTaskLists().toString() + "(" + lowerBound + ")";
	}
	
	@Override
	public int compareTo(TreeSchedule o) {
		return getLowerBound() - o.getLowerBound();
	}

	public TaskGraph getGraph() {
		return this.graph;
	}

	public Set<Task> getAllocated() {
		Set<Task> allocated = new HashSet<>();
		
		for(TreeSchedule s = this; !s.isEmpty(); s = s.parent) {
			allocated.add(s.task);
		}
		
		return allocated;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TreeSchedule)) {
			return false;
		}
		
		TreeSchedule other = (TreeSchedule) obj;
		
		if(isEmpty()) {
			return other.isEmpty() && graph.equals(other.graph);
		} else {
			return !other.isEmpty() && graph.equals(other.graph) && Objects.equals(parent, other.parent) && task == other.task && processor == other.processor;
		}
	}
	
	@Override
	public int hashCode() {
		if(isEmpty()) {
			return Objects.hash(graph);
		} else {
			return Objects.hash(graph, parent, task, processor);
		}
	}
	
	public Set<Task> getAllocatable() {
		Set<Task> allocated = getAllocated();
		Set<Task> results = new HashSet<>();
		
		for(Task t : graph.getAll()) {
			if(allocated.contains(t)) {
				continue;
			}
				
			boolean allParents = true;
			for(Dependency dep : t.getParents()) {
				Task parent = dep.getSource();
				if(!allocated.contains(parent)) {
					allParents = false;
					break;
				}
			}
			
			if(allParents) {
				results.add(t);
			}
		}
		
		return results;
	}

	/** This gets the number of used processor. A processor is considered used if
	 * there is a task allocated on it, or on some processor number larger than it. */
	public int getNumberOfUsedProcessors() {
		int n = 0;
		for(TreeSchedule s = this; !s.isEmpty(); s = s.parent) {
			if(n < s.processor) {
				n = s.processor;
			}
		}
		
		return n;
	}
}
