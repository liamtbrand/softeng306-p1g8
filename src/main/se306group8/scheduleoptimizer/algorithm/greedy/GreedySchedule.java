package se306group8.scheduleoptimizer.algorithm.greedy;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * implementation of Schedule for the greedy algorithm
 */
class GreedySchedule implements Schedule {

	private TaskGraph graph;
	private final int numUsedProcessors;
	private List<List<Task>> interalSchedule;
	private final int totalRuntime;
	private final Map<Task, ProcessAllocation> allocations;

	GreedySchedule(TaskGraph taskGraph, Map<Task, ProcessAllocation> allocations, List<List<Task>> interalSchedule) {
		this.allocations = allocations;
		this.interalSchedule = interalSchedule;
		graph = taskGraph;

		// precalc stats about schedule and store the results
		int totalRuntime = 0;
		for (Task t : taskGraph.getAll()) {
			int endTime = allocations.get(t).endTime;
			if (totalRuntime < endTime) {
				totalRuntime = endTime;
			}
		}

		this.totalRuntime = totalRuntime;

		// find number of used processors in the greedy implementation i used
		// lower process numbers first so I can break after I see a unused processor
		int numUsedProcessors = 0;
		for (List<Task> processor : interalSchedule) {
			if (processor.isEmpty() == false) {
				numUsedProcessors++;
			} else {
				break;
			}
		}
		this.numUsedProcessors = numUsedProcessors;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskGraph getGraph() {
		return graph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfUsedProcessors() {
		return numUsedProcessors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Task> getTasksOnProcessor(int processor) {
		// Internally I used processors start at 0 but schedules have processors start
		// at 1
		processor--;
		return interalSchedule.get(processor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<List<Task>> iterator() {
		return interalSchedule.listIterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getStartTime(Task task) {
		return allocations.get(task).startTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getProcessorNumber(Task task) {
		// Internally I used processors start at 0 but schedules have processors start
		// at 1
		return allocations.get(task).processor + 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTotalRuntime() {
		return totalRuntime;
	}

}
