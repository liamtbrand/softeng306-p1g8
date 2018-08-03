package se306group8.scheduleoptimizer.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;

import se306group8.scheduleoptimizer.algorithm.ListSchedule.ProcessorAllocation;
import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class GreedyChildScheduleFinder implements ChildScheduleFinder{

	// rather than recalcuate bottom level every time we cache the last result
	private TaskGraph lastGraph;

	// we can allocate by remembering parent
	private TreeSchedule lastSchedule;
	private Set<Task> allocated;

	private List<Task> allocatable;

	private int numProcessors;

	public GreedyChildScheduleFinder(int numProcessors) {
		this.numProcessors = numProcessors;
		allocated = new HashSet<Task>();
		allocatable = new ArrayList<Task>();
	}

	@Override
	public List<TreeSchedule> getChildSchedules(TreeSchedule schedule) {
//		Collection<TreeSchedule> unorderedSet = super.getChildSchedules(schedule);
//		List<TreeSchedule> unorderedList = (List<TreeSchedule>)unorderedSet;
//		List<TreeSchedule> orderedList = orderByLowerBound(unorderedList);
//		return orderedList;

		allocated = schedule.getAllocated();
		allocatable = new ArrayList<>(schedule.getAllocatable());

		// Important optimization step needs to be done after calling compute fucntions
		lastSchedule = schedule;
		lastGraph = schedule.getGraph();
		
		// Robert's first Java lambda
		// ordered such that largest bottom order first
		allocatable.sort((Task t1, Task t2) -> schedule.getGraph().getBottomTime(t2) - schedule.getGraph().getBottomTime(t1));

		List<TreeSchedule> children = new ArrayList<TreeSchedule>();

		// use the schedule to get timing info
		// I didn't use getFullSchedule() because comments says it may return null
		//Schedule parentSchedule = new ListSchedule(schedule.getGraph(), schedule.computeTaskLists());
		for (Task task : allocatable) {
			children.addAll(greedyChildren(lastSchedule, task));
		}
		
		return children;

	}

	private List<TreeSchedule> greedyChildren(TreeSchedule parentSchedule, Task task) {
		
		int processorsToAllocate = 0;
		for (int p= 1;p<=numProcessors;p++) {
			ProcessorAllocation pA =parentSchedule.getLastAllocationForProcessor(p);
			processorsToAllocate++;
			if (pA == null) {
				break;
			}
		}

		// low memory map [][0] is TreeSchedule [][1] is start time of new task
		Object[][] startTimes = new Object[processorsToAllocate][2];

		for (int p = 1; p <= processorsToAllocate; p++) {
			TreeSchedule childSchedule = new TreeSchedule(lastGraph, task, p, lastSchedule);
			startTimes[p - 1][0] = childSchedule;
			startTimes[p - 1][1] = computeStartTime(parentSchedule, task, p);
		}
		
		//sort by start times
		Arrays.sort(startTimes,(Object[] o1, Object[] o2) -> (Integer)o1[1]- (Integer)o2[1]);
		List<TreeSchedule> childrenSchedules = new ArrayList<TreeSchedule>();
		
		for (int p = 0; p < processorsToAllocate; p++) {
			childrenSchedules.add((TreeSchedule)startTimes[p][0]);
		}
		
		return childrenSchedules;
	}

	private int computeStartTime(TreeSchedule parentSchedule, Task task, int p) {
		
		ProcessorAllocation lastAllocation = lastSchedule.getLastAllocationForProcessor(p);
		int processorEndtime;
		if (lastAllocation == null) {
			processorEndtime = 0;
		}else {
			processorEndtime= lastSchedule.getLastAllocationForProcessor(p).endTime;
		}
		
		
		// in case the task before is not a dependency
		int startTime = processorEndtime;

		for (Dependency dep : task.getParents()) {
			Task parent = dep.getSource();

			// because we looped by partial order the parent will already be allocated
			ProcessorAllocation parentAllocation = lastSchedule.getAlloctionFor(parent);
			int time;

			// time start rules
			if (parentAllocation.processor == p) {
				time = processorEndtime;
			} else {
				int comStart = parentAllocation.endTime + dep.getCommunicationCost();
				time = (comStart > processorEndtime) ? comStart : processorEndtime;
			}

			// we have to accommodate for the worst dependency
			if (time > startTime) {
				startTime = time;
			}
		}

		return startTime;
	}
}
