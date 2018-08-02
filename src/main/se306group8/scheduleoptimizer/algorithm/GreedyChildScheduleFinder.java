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

import se306group8.scheduleoptimizer.algorithm.greedy.ProcessAllocation;
import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class GreedyChildScheduleFinder implements ChildScheduleFinder{

	// rather than recalcuate bottom level every time we cache the last result
	private TaskGraph lastGraph;
	private Map<Task, Integer> bottomLevel;

	// we can allocate by remembering parent
	private TreeSchedule lastSchedule;
	private Set<Task> allocated;

	private List<Task> allocatable;

	private int numProcessors;

	public GreedyChildScheduleFinder(int numProcessors) {
		this.numProcessors = numProcessors;
		// allocated = new HashSet<Task>();
	}

	@Override
	public List<TreeSchedule> getChildSchedules(TreeSchedule schedule) {
//		Collection<TreeSchedule> unorderedSet = super.getChildSchedules(schedule);
//		List<TreeSchedule> unorderedList = (List<TreeSchedule>)unorderedSet;
//		List<TreeSchedule> orderedList = orderByLowerBound(unorderedList);
//		return orderedList;

		allocated = computeAllocated(schedule);
		bottomLevel = computeBottomLevel(schedule);
		allocatable = computeAllocatableTasks(schedule, allocated);

		// Important optimization step needs to be done after calling compute fucntions
		lastSchedule = schedule;

		// Robert's first Java lambda
		// ordered such that largest bottom order first
		allocatable.sort((Task t1, Task t2) -> bottomLevel.get(t2) - bottomLevel.get(t1));

		List<TreeSchedule> children = new ArrayList<TreeSchedule>();

		// use the schedule to get timing info
		// I didn't use getFullSchedule() because comments says it may return null
		Schedule parentSchedule = new ListSchedule(schedule.getGraph(), schedule.computeTaskLists());
		for (Task task : allocatable) {
			children.addAll(greedyChildren(parentSchedule, task));
		}
		
		return children;

	}

	private List<TreeSchedule> greedyChildren(Schedule parentSchedule, Task task) {
		
		int usedProcessors = parentSchedule.getNumberOfUsedProcessors();

		// we don't need to allocate to two free processors
		int processorsToAllocate = (usedProcessors < numProcessors) ? usedProcessors + 1 : numProcessors;

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

	private int computeStartTime(Schedule parentSchedule, Task task, int p) {
		// in case the task before is not a dependency
		int startTime = processorEndtime[processor];

		for (Dependency dep : task.getParents()) {
			Task parent = dep.getSource();

			// because we looped by partial order the parent will already be allocated
			ProcessAllocation parentAllocation = allocations.get(parent);
			int time;

			// time start rules
			if (parentAllocation.processor == processor) {
				time = processorEndtime[processor];
			} else {
				int comStart = parentAllocation.endTime + dep.getCommunicationCost();
				time = (comStart > processorEndtime[processor]) ? comStart : processorEndtime[processor];
			}

			// we have to accommodate for the worst dependency
			if (time > startTime) {
				startTime = time;
			}
		}

		return startTime;
	}

	private List<Task> computeAllocatableTasks(TreeSchedule schedule, Set<Task> allocated) {
		if (schedule.getParent() == lastSchedule) {
			allocatable.remove(schedule.getMostRecentTask());
		} else {
			allocatable = new ArrayList<Task>();
			for (Task task : schedule.getGraph().getAll()) {
				if (!allocated.contains(task)) {
					boolean allocate = true;
					for (Dependency dep : task.getParents()) {
						if (!allocated.contains(dep.getSource())) {
							allocate = false;
							break;
						}
					}
					if (allocate) {
						allocatable.add(task);
					}
				}
			}
		}

		return allocatable;

	}

	private Set<Task> computeAllocated(TreeSchedule schedule) {
		if (schedule.getParent() == lastSchedule) {
			allocated.add(schedule.getMostRecentTask());
		} else {
			List<List<Task>> taskList = schedule.computeTaskLists();
			allocated = new HashSet<Task>();
			for (List<Task> processor : taskList) {
				for (Task task : processor) {
					allocated.add(task);
				}
			}
		}

		return allocated;
	}

	private Map<Task, Integer> computeBottomLevel(TreeSchedule schedule) {
		if (lastGraph != schedule.getGraph()) {
			lastGraph = schedule.getGraph();
			bottomLevel = new HashMap<Task, Integer>();

			Collection<Task> roots = lastGraph.getRoots();

			// In computing the bottom level of all roots we get the bottom level of all
			// tasks
			for (Task root : roots) {
				dynamicComputeBottomLevel(root);
			}

		}
		return bottomLevel;
	}

	private int dynamicComputeBottomLevel(Task task) {
		Integer bottomLevelValue = bottomLevel.get(task);
		if (bottomLevelValue != null) {
			return bottomLevelValue;
		}

		int childBottom = 0;
		for (Dependency dep : task.getChildren()) {
			Task child = dep.getTarget();
			int childBottomLevel = dynamicComputeBottomLevel(child);
			if (childBottomLevel > childBottom) {
				childBottom = childBottomLevel;
			}
		}
		bottomLevelValue = childBottom + task.getCost();
		bottomLevel.put(task, bottomLevelValue);
		return bottomLevelValue;
	}

	private List<TreeSchedule> orderByLowerBound(List<TreeSchedule> list) {
		Collections.sort(list);
		return list;
	}

}
