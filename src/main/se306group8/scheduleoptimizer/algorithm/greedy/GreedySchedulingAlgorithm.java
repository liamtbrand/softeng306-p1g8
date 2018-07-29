package se306group8.scheduleoptimizer.algorithm.greedy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.MockRuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.dotfile.ScheduleFromFile;
import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Non optimal greedy algorithm for task scheduling
 */
public class GreedySchedulingAlgorithm implements Algorithm {

	// TODO use the monitor
	RuntimeMonitor monitor;

	// for these internal data structures the convention is processors start at 0
	private Map<Task, ProcessAllocation> allocations;
	private int[] processorEndtime;
	private List<List<Task>> schedule;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {

		// reset
		allocations = new HashMap<Task, ProcessAllocation>();
		schedule = new ArrayList<List<Task>>(numberOfProcessors);
		processorEndtime = new int[numberOfProcessors];

		// init schedule
		for (int i = 0; i < numberOfProcessors; i++) {
			schedule.add(new ArrayList<Task>());
		}

		// requires getAll to return a partial order
		List<Task> partialOrder = graph.getAll();

		// allocate tasks
		for (Task task : partialOrder) {
			allocatePosition(task);
		}

		return new GreedySchedule(graph, allocations, schedule);

	}

	/**
	 * Finds the best position to put the task in the current schedule
	 * 
	 * @param task Task to allocate
	 */
	private void allocatePosition(Task task) {

		// we don't know the best time and processor at this stage
		int bestTime = 0;
		int bestProcessor = 0;

		// internally we use the convention processors start at 0
		for (int pNo = 0; pNo < schedule.size(); pNo++) {

			int startTime = timeAtPosition(task, pNo);

			// if we are at processor 0 we have no bestTime to compare against
			if (startTime < bestTime || pNo == 0) {
				bestTime = startTime;
				bestProcessor = pNo;
			}

			if (schedule.get(pNo).isEmpty()) {
				// all processors after this are empty as well
				break;
			}

		}

		// now we have found best processor to put task
		processorEndtime[bestProcessor] = bestTime + task.getCost();
		ProcessAllocation greedyAllocation = new ProcessAllocation(bestTime, processorEndtime[bestProcessor],
				bestProcessor);
		allocations.put(task, greedyAllocation);
		schedule.get(bestProcessor).add(task);
	}

	/**
	 * Finds the starting time a task would take if it were added to that processor
	 * with the current schedule
	 * 
	 * @param task
	 * @param processor
	 * @return
	 */
	private int timeAtPosition(Task task, int processor) {
		
		//in case the task before is not a dependency
		int startTime = processorEndtime[processor];

		for (Dependency dep : task.getParents()) {
			Task parent = dep.getSource();
			
			//because we looped by partial order the parent will already be allocated
			ProcessAllocation parentAllocation = allocations.get(parent);
			int time;
			
			//time start rules
			if (parentAllocation.processor == processor) {
				time = processorEndtime[processor];
			} else {
				int comStart = parentAllocation.endTime + dep.getCommunicationCost();
				time = (comStart > processorEndtime[processor]) ? comStart : processorEndtime[processor];
			}

			//we have to accommodate for the worst dependency
			if (time > startTime) {
				startTime = time;
			}
		}

		return startTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		this.monitor = monitor;

	}

}
