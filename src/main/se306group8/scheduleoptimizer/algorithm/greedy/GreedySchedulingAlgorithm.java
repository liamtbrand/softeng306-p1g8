package se306group8.scheduleoptimizer.algorithm.greedy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.CLIRuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.ListSchedule;
import se306group8.scheduleoptimizer.algorithm.ListSchedule.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
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
	private Map<Task, ListSchedule.ProcessorAllocation> allocations;
	private int[] processorEndtime;
	private List<List<Task>> schedule;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		// Create new RuntimeMonitor instance
		if (this.monitor == null) {
			this.monitor = new CLIRuntimeMonitor(numberOfProcessors);
		}
		
		// Invoke start() method on RuntimeMonitor instance
		this.monitor.start();

		// reset
		allocations = new HashMap<>();
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
		
		// Invoke finish() method on RuntimeMonitor instance
		monitor.finish();

		return new ListSchedule(graph, allocations, schedule);

	}

	/**
	 * Finds the best position to put the task in the current schedule
	 * 
	 * @param task Task to allocate
	 */
	private void allocatePosition(Task task) {

		// we don't know the best time and processor at this stage
		int bestTime = 0;
		int bestProcessor = 1;

		// internally we use the convention processors start at 0
		for (int pNo = 1; pNo <= schedule.size(); pNo++) {

			int startTime = timeAtPosition(task, pNo);

			// if we are at processor 1 we have no bestTime to compare against
			if (startTime < bestTime || pNo == 1) {
				bestTime = startTime;
				bestProcessor = pNo;
			}

			if (schedule.get(pNo - 1).isEmpty()) {
				// all processors after this are empty as well
				break;
			}

		}

		// now we have found best processor to put task
		processorEndtime[bestProcessor - 1] = bestTime + task.getCost();
		ProcessorAllocation greedyAllocation = new ProcessorAllocation(bestTime, processorEndtime[bestProcessor - 1], bestProcessor);
		allocations.put(task, greedyAllocation);
		schedule.get(bestProcessor - 1).add(task);
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
		int startTime = processorEndtime[processor - 1];

		for (Dependency dep : task.getParents()) {
			Task parent = dep.getSource();
			
			//because we looped by partial order the parent will already be allocated
			ProcessorAllocation parentAllocation = allocations.get(parent);
			int time;
			
			//time start rules
			if (parentAllocation.processor == processor) {
				time = processorEndtime[processor - 1];
			} else {
				int comStart = parentAllocation.endTime + dep.getCommunicationCost();
				time = (comStart > processorEndtime[processor - 1]) ? comStart : processorEndtime[processor - 1];
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
