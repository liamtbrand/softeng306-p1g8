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

public class GreedySchedulingAlgorithm implements Algorithm {
	
	RuntimeMonitor monitor; 
	
	
	private Map<Task, ProcessAllocation> allocations; 
	private int[] processorStart;
	
	public GreedySchedulingAlgorithm() {
		//monitor= new MockRuntimeMonitor();
	}

	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		allocations = new HashMap<Task, ProcessAllocation>();
		List<List<Task>> schedule = new ArrayList<List<Task>>(numberOfProcessors);
		for (int i=0;i<numberOfProcessors;i++) {
			schedule.add( new ArrayList<Task>());
		}
		
		List<Task> partialOrder = graph.getAll();
		processorStart = new int[schedule.size()];
		
		for (Task task:partialOrder) {
			allocatePosition(schedule,task);
		}
		
		return new GreedySchedule(graph, allocations, schedule);
		
	}

	private void allocatePosition(List<List<Task>> schedule, Task task) {
		
		int bestTime = 0;
		int bestProcessor = 0;
		
		for (int i=0;i<schedule.size();i++) {
			int startTime =0;
			
			for (Dependency dep:task.getParents()) {
				Task parent = dep.getSource();
				ProcessAllocation parentAllocation = allocations.get(parent);
				int time;
				if (parentAllocation.processor==i) {
					time = processorStart[i];
				}else {
					int comStart = parentAllocation.endTime + dep.getCommunicationCost();
					time = (comStart>processorStart[i])?comStart:processorStart[i];
				}
				
				if (time > startTime) {
					startTime=time;
				}
			}
			
			if (startTime < bestTime || i==0) {
				bestTime = startTime;
				bestProcessor = i;
			}
			
			if (schedule.get(i).isEmpty()) {
				//all processors after this are empty as well
				break;
			}
			
		}
		processorStart[bestProcessor]=bestTime+task.getCost();
		ProcessAllocation greedyAllocation = new ProcessAllocation(bestTime,processorStart[bestProcessor],bestProcessor);
		allocations.put(task, greedyAllocation);
		schedule.get(bestProcessor).add(task);
	}

	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		this.monitor=monitor;
		
	}

}
