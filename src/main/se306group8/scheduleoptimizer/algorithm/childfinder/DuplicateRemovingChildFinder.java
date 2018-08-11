package se306group8.scheduleoptimizer.algorithm.childfinder;

import java.util.ArrayList;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class DuplicateRemovingChildFinder implements ChildScheduleFinder {
	private final int processors;
	
	public DuplicateRemovingChildFinder(int processors) {
		this.processors = processors;
	}
	
	@Override
	public List<TreeSchedule> getChildSchedules(TreeSchedule schedule) {
		List<TreeSchedule> childrenSchedules = new ArrayList<>();
		
		for (Task task : schedule.getAllocatable()) {
			int processorsToAllocate;
			if(task.getId() > schedule.getLargestRoot()) {
				processorsToAllocate = Math.min(schedule.getNumberOfUsedProcessors() + 1, processors);
			} else {
				processorsToAllocate = schedule.getNumberOfUsedProcessors();
			}
			
			for (int p = 1; p <= processorsToAllocate; p++) {
				TreeSchedule childSchedule = new TreeSchedule(task, p, schedule);
				childrenSchedules.add(childSchedule);
			}
		}

		return childrenSchedules;
	}
}