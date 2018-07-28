package se306group8.scheduleoptimizer.algorithm.greedy;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

class GreedySchedule implements Schedule{

	private TaskGraph graph;
	private final int numUsedProcessors;
	private List<List<Task>> interalSchedule;
	private final int totalRuntime;
	private final Map<Task,ProcessAllocation> allocations;
	
	GreedySchedule(TaskGraph taskGraph, Map<Task,ProcessAllocation> allocations,List<List<Task>> interalSchedule ){
		this.allocations=allocations;
		this.interalSchedule = interalSchedule;
		graph=taskGraph;
		
		//precalc stats about schedule and store the results
		int totalRuntime = 0;
		for (Task t: taskGraph.getAll()) {
			int endTime = allocations.get(t).endTime;
			if (totalRuntime < endTime) {
				totalRuntime = endTime;
			}
		}
		this.totalRuntime=totalRuntime;
		int numUsedProcessors=0;
		for (List<Task> processor:interalSchedule ) {
			if (processor.isEmpty()==false) {
				numUsedProcessors++;
			}else {
				break;
			}
		}
		this.numUsedProcessors=numUsedProcessors;
		
	}
	
	
	@Override
	public TaskGraph getGraph() {
		return graph;
	}

	@Override
	public int getNumberOfUsedProcessors() {
		return numUsedProcessors;
	}

	@Override
	public List<Task> getTasksOnProcessor(int processor) {
		processor--;//one based indexing on processors
		return interalSchedule.get(processor);
	}

	@Override
	public ListIterator<List<Task>> iterator() {
		return interalSchedule.listIterator();
	}

	@Override
	public int getStartTime(Task task) {
		return allocations.get(task).startTime;
	}

	@Override
	public int getProcessorNumber(Task task) {
		//Internally I used processors start at 0 but schedules have processors start at 1
		return allocations.get(task).processor+1;
	}

	@Override
	public int getTotalRuntime() {
		return totalRuntime;
	}

}
