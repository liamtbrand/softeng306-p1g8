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
			int processorsToAllocate;
			if(task.getId() > schedule.getLargestRoot()) {
				processorsToAllocate = Math.min(schedule.getNumberOfUsedProcessors() + 1, processors);
			} else {
				processorsToAllocate = schedule.getNumberOfUsedProcessors();
			}
			
			for (int p = 1; p <= processorsToAllocate; p++) {
				if(isCorrectIndependantTaskOrder(task, p, schedule) && isBestParent(task, p, schedule)) {
					childrenSchedules.add(new TreeSchedule(task, p, schedule));
				}
			}
		}

		return childrenSchedules;
	}
	
	private boolean isCorrectIndependantTaskOrder(Task task, int processor, TreeSchedule schedule) {
		if(!task.isIndependant())
			return true;
		
		ProcessorAllocation alloc = schedule.getLastAllocationForProcessor(processor);
		return alloc == null || !alloc.task.isIndependant() || alloc.task.getId() < task.getId();
	}		
	
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