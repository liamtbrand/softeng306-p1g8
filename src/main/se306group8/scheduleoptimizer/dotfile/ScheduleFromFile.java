package se306group8.scheduleoptimizer.dotfile;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/**
 * Represents an allocation of tasks to different processors in a specific order. 
 * 
 * This is the result object that is returned from the algorithm.
 */
public final class ScheduleFromFile implements Schedule {
	private class ProcessAllocation {
		final int startTime;
		final int processor;
		final int endTime;
		
		public ProcessAllocation(int startTime, int endTime, int processor) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.processor = processor;
		}
	}
	
	private final TaskGraph graph;
	private final List<List<Task>> taskLists;
	private final Map<Task, ProcessAllocation> allocations; 
	
	/**
	 * Creates a schedule.
	 * 
	 * @param graph The task graph that the schedule was generated from.
	 * @param taskLists The list of tasks that is assigned to each processor. Each task must be included in the graph, and each task must
	 *        be used once and only once.
	 */
	ScheduleFromFile(TaskGraph graph, List<List<Task>> taskLists) {
		assert graph != null && taskLists != null;
		
		this.graph = graph;
		this.taskLists = taskLists;
		allocations = new HashMap<>();
	}

	private ProcessAllocation computeAllocation(Task task) {
		ProcessAllocation alloc = allocations.get(task);
		
		if(alloc != null) {
			return alloc;
		}
		
		int processor = 0;
		for(List<Task> list : taskLists) {
			if(list.contains(task)) {
				break;
			}
			
			processor++;
		}
		
		int startTime = 0;
		
		for(Dependency dep : task.getParents()) {
			int time;
			
			Task otherTask = dep.getSource();
			ProcessAllocation otherAlloc = computeAllocation(otherTask);
			
			if(processor != otherAlloc.processor) {
				time = otherAlloc.startTime + otherTask.getCost() + dep.getCommunicationCost();
			} else {
				time = otherAlloc.startTime + otherTask.getCost();
			}
			
			if(time > startTime) {
				startTime = time;
			}
		}
		
		alloc = new ProcessAllocation(startTime, startTime + task.getCost(), processor);
		allocations.put(task, alloc);
		
		return alloc;
	}
	
	public TaskGraph getGraph() {
		return graph;
	}
	
	public int getNumberOfUsedProcessors() {
		return taskLists.size();
	}
	
	public List<Task> getTasksOnProcessor(int processor) {
		return taskLists.get(processor);
	}

	@Override
	public ListIterator<List<Task>> iterator() {
		return taskLists.listIterator();
	}
	
	public int getStartTime(Task task) {
		return computeAllocation(task).startTime;
	}
	
	public int getProcessorNumber(Task task) {
		return computeAllocation(task).processor;
	}
	
	public int getTotalRuntime() {
		//The maximum runtime is the last end time of a particular tasks.
		
		return taskLists.stream()				//Creates a stream of task Lists
				.flatMap(List<Task>::stream)	//Creates a stream of streams of tasks
				.map(this::computeAllocation)	//Map each task to the allocation object
				.mapToInt(x -> x.endTime)		//Extracts the endTime from each allocation
				.max()							//Gets the maximum endTime
				.orElse(0);						//If there are no items return 0
	}
}
