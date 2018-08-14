package se306group8.scheduleoptimizer.algorithm.childfinder;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class DuplicateRemovingChildFinder implements ChildScheduleFinder {
	private final int processors;
	public DuplicateRemovingChildFinder(int processors) {
		this.processors = processors;
	}
	
	@Override
	public List<TreeSchedule> getChildSchedules(TreeSchedule schedule) {
		//Only add the schedule if it is the earliest schedule that can create it.
		//Schedule a is smaller than schedule b if 
		List<TreeSchedule> childrenSchedules = new ArrayList<>(processors * schedule.getAllocatable().size());
		
		for (Task task : schedule.getAllocatable()) {
			//Make sure that the processors are allocated in the order of the first task on each processor.
			int processorsToAllocate;
			if(task.getId() > schedule.getLargestRoot()) {
				processorsToAllocate = Math.min(schedule.getNumberOfUsedProcessors() + 1, processors);
			} else {
				processorsToAllocate = schedule.getNumberOfUsedProcessors();
			}

			for (int p = 1; p <= processorsToAllocate; p++) {
				if(isBestParent(task, p, schedule) && checkHorizon(task, p, schedule)) {
					childrenSchedules.add(new TreeSchedule(task, p, schedule));
				}
			}
		}

		return childrenSchedules;
	}
	
	private boolean checkHorizon(Task task, int processor, TreeSchedule schedule) {
		//If these two tasks can be swapped
		int positionOfNewTask = schedule.getNumberOfTasksOnProcessor(processor); //The 0 indexed position of the new task
		
		//Decrement position until index order has been reached. If at any of those positions a same of better schedule is found
		//return false, else return true;
		
		List<ProcessorAllocation> allocations = new ArrayList<>(schedule.getNumberOfTasksOnProcessor(processor)); //The list of tasks other than the scheduled task on this processor
		
		ProcessorAllocation alloc = schedule.getLastAllocationForProcessor(processor);
		while(alloc != null) {
			allocations.add(alloc);
		}
		
		int[] startTimes = new int[schedule.getGraph().getAll().size()];
		for(ProcessorAllocation t : allocations) {
			startTimes[t.task.getId()] = t.startTime;
		}
		
		//Move the new task to the position before the swapTarget
		swap:
		for(int swapTarget = allocations.size() - 1; swapTarget >= 0; swapTarget--) {
			Task target = allocations.get(swapTarget).task;
			if(task.isParent(target) || task.getId() > target.getId()) { //We can't swap, this ordering is valid.
				return true;
			}
			
			int taskStartTime = schedule.getDataReadyTime(task);
			if(swapTarget != 0) {
				taskStartTime = Math.max(allocations.get(swapTarget - 1).endTime, taskStartTime);
			}
			
			//Populate the start times for the other tasks
			//Only worry about tasks ahead of the swap, and the task itself
			for(int p = swapTarget; p < allocations.size(); p++) {
				int startTime = schedule.getDataReadyTime(allocations.get(p).task);
				if(p == swapTarget) {
					startTime = Math.max(startTime, taskStartTime + task.getCost());
				} else {
					startTime = Math.max(startTime, startTimes[allocations.get(p - 1).task.getId()] + allocations.get(p - 1).task.getCost());
				}
				
				startTimes[allocations.get(p).task.getId()] = startTime;
				
				if(startTime <= allocations.get(p).startTime) {
					return false; //This task is scheduled the same or better. Therefore all tasks after it will also be scheduled the same or better
				} else if(startTime > schedule.getRequiredBy(allocations.get(p).task)) {
					continue swap; //This item was scheduled too late. The swap may not be valid. Continue to next swap
				} else {
					//Check the comms to tasks that have not been scheduled yet.
					
				}
			}
		}
	}
	
	//Ensures that the parent schedule is the earliest schedule that can produce this child.
	private boolean isBestParent(Task task, int processor, TreeSchedule schedule) {
		//Look at each task on the top of the processor, if it is a later task then the schedule is not valid, provided removing it leaves a valid schedule.
		for(int p = 1; p <= schedule.getNumberOfUsedProcessors(); p++) {
			if(p != processor) {
				ProcessorAllocation alloc = schedule.getLastAllocationForProcessor(p);

				if(alloc.task.getId() <= task.getId()) {
					continue;
				}

				if(p != schedule.getNumberOfUsedProcessors() && schedule.getNumberOfTasksOnProcessor(p) == 1) {
					//We can't remove this one, as it would leave a processor free in the middle of the run
					continue;
				}

				if(!schedule.isRemovable(alloc.task) || task.isParent(alloc.task)) {
					continue; //This task is needed for child - parent reasons
				}

				return false;
			}
		}
		
		return true;
	}
}