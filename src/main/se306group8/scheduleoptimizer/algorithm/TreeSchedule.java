package se306group8.scheduleoptimizer.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * This class represents a partial schedule. It stores a parent schedule, and
 * the task and processor that was added to it. This allows a nice easy to work
 * with model of a schedule.
 */
public class TreeSchedule implements Comparable<TreeSchedule> {

	// Constructed fields
	private final TreeSchedule parent;

	/** If this is null then this is the empty schedule */
	private final TaskGraph graph;
	private final MinimumHeuristic heuristic;

	// Calculated fields
	private final ProcessorAllocation allocation;
	/** A bound that any full solution using this solution must be greater than. */
	private final int lowerBound;
	private final int idleTime;
	private final int numberOfUsedProcessors;
	private final int runtime;

	// Per task arrays
	private final ProcessorAllocation[] allocations;
	private final int[] numberOfParentsUncheduled;

	// Per processor arrays
	/** This array.length = numberOfUsedProcessors */
	private final ProcessorAllocation[] lastAllocationOnProcessor;

	// Sets
	private final Collection<Task> allocatable;
	private final Collection<Task> allocated;

	/**
	 * Creates an empty schedule.
	 * 
	 * @param heuristic The heuristic that is used to calculate the lower bound for
	 *                  this schedule and all children.
	 */
	public TreeSchedule(TaskGraph graph, MinimumHeuristic heuristic) {
		assert graph != null;
		int numberOfTasks = graph.getAll().size();

		this.graph = graph;
		this.allocation = null;
		this.parent = null;
		this.heuristic = heuristic;

		idleTime = 0;
		numberOfUsedProcessors = 0;
		allocations = new ProcessorAllocation[numberOfTasks];
		runtime = 0;

		numberOfParentsUncheduled = new int[numberOfTasks];
		for (Task task : graph.getAll()) {
			numberOfParentsUncheduled[task.getId()] = task.getParents().size();
		}

		lastAllocationOnProcessor = new ProcessorAllocation[0];

		allocatable = graph.getRoots();
		allocated = Collections.emptyList();

		lowerBound = heuristic.estimate(this);
	}

	/**
	 * Creates a schedule from a parent schedule and an allocation.
	 */
	public TreeSchedule(Task task, int processor, TreeSchedule parent) {
		this.graph = parent.graph;
		this.parent = parent;
		this.heuristic = parent.heuristic;

		numberOfUsedProcessors = Math.max(parent.numberOfUsedProcessors, processor);
		numberOfParentsUncheduled = parent.numberOfParentsUncheduled.clone();
		allocatable = new ArrayList<>();
		lastAllocationOnProcessor = Arrays.copyOf(parent.lastAllocationOnProcessor, numberOfUsedProcessors);
		allocations = parent.allocations.clone();

		for (Dependency dep : task.getChildren()) {
			Task child = dep.getTarget();
			numberOfParentsUncheduled[child.getId()]--;

			if (numberOfParentsUncheduled[child.getId()] == 0) {
				allocatable.add(child);
			}
		}

		int processorReadyTime;

		if (parent.getLastAllocationForProcessor(processor) != null) {
			processorReadyTime = parent.getLastAllocationForProcessor(processor).endTime;
		} else {
			processorReadyTime = 0;
		}

		int startTime = processorReadyTime;

		for (Dependency dep : task.getParents()) {
			Task parentTask = dep.getSource();
			ProcessorAllocation alloc = parent.getAlloctionFor(parentTask);
			int dataReadyTime;

			if (alloc.processor == processor) {
				dataReadyTime = alloc.endTime;
			} else {
				dataReadyTime = alloc.endTime + dep.getCommunicationCost();
			}

			if (dataReadyTime > startTime) {
				startTime = dataReadyTime;
			}
		}

		idleTime = parent.idleTime + startTime - processorReadyTime;

		for (Task oldAllocatable : parent.allocatable) {
			if (oldAllocatable != task) {
				allocatable.add(oldAllocatable);
			}
		}

		allocation = new ProcessorAllocation(task, startTime, processor);

		lastAllocationOnProcessor[processor - 1] = allocation;
		allocations[task.getId()] = allocation;

		allocated = new ArrayList<>(parent.allocated);
		allocated.add(task);

		runtime = Math.max(parent.runtime, allocation.endTime);
		lowerBound = allocatable.isEmpty() ? runtime : heuristic.estimate(this);
	}

	/** Returns true if the schedule is empty */
	public boolean isEmpty() {
		return allocation == null;
	}

	/** Returns the amount of time wasted */
	public int getIdleTime() {
		return idleTime;
	}

	public int getRuntime() {
		return runtime;
	}

	/**
	 * Returns the ProcessorAllocation instance that this task was scheduled on.
	 * This returns null if the task has not been scheduled.
	 */
	public ProcessorAllocation getAlloctionFor(Task t) {
		return allocations[t.getId()];
	}

	/**
	 * Returns the last processor allocation on a processor null if no allocation
	 * 
	 * @param processor
	 * @return
	 */
	public ProcessorAllocation getLastAllocationForProcessor(int processor) {
		if (processor > lastAllocationOnProcessor.length)
			return null;

		return lastAllocationOnProcessor[processor - 1];
	}

	public List<List<Task>> computeTaskLists() {
		if (isEmpty()) {
			return new ArrayList<>();
		}

		List<List<Task>> result;
		if (parent.isEmpty()) {
			result = new ArrayList<>();
		} else {
			result = parent.computeTaskLists();
		}

		while (result.size() < numberOfUsedProcessors) {
			result.add(new ArrayList<>());
		}

		result.get(allocation.processor - 1).add(allocation.task);

		return result;
	}

	public boolean isComplete() {
		int tasks = 0;
		for (TreeSchedule s = this; !s.isEmpty(); s = s.parent) {
			tasks++;
		}

		return tasks == graph.getAll().size();
	}

	/**
	 * Returns the full schedule. This may be null if this solution is not a full
	 * solution.
	 */
	public Schedule getFullSchedule() {
		if (!isComplete())
			return null;

		return new ListSchedule(graph, computeTaskLists());
	}

	public ProcessorAllocation getMostRecentAllocation() {
		return allocation;
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

	public Collection<Task> getAllocated() {
		return allocated;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TreeSchedule)) {
			return false;
		}

		TreeSchedule other = (TreeSchedule) obj;

		if (isEmpty()) {
			return other.isEmpty() && graph.equals(other.graph);
		} else {
			return !other.isEmpty() && graph.equals(other.graph) && Objects.equals(parent, other.parent)
					&& allocation.equals(other.allocation);
		}
	}

	@Override
	public int hashCode() {
		if (isEmpty()) {
			return Objects.hash(graph);
		} else {
			return Objects.hash(graph, parent, allocation.processor, allocation.task);
		}
	}

	public Collection<Task> getAllocatable() {
		return allocatable;
	}

	/**
	 * This gets the number of used processor. A processor is considered used if
	 * there is a task allocated on it, or on some processor number larger than it.
	 */
	public int getNumberOfUsedProcessors() {
		return numberOfUsedProcessors;
	}
}
