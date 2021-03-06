package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * 
 * TaskGraph is an immutable representation of the DAG, including the weight annotations.
 *
 */
public final class TaskGraph {
	private final List<Task> topologicalOrder;
	private final Collection<Task> roots;
	private final Collection<Dependency> edges;
	private final String name;
	private final int[] bottomTime;
	private final Task[] taskMap;
	private final int totalTime;
	
	TaskGraph(String name, Collection<Task> tasks) {
		assert tasks.stream().noneMatch(Objects::isNull);
		
		topologicalOrder = new ArrayList<>();
		roots = new ArrayList<>();
		this.name = name;
		this.taskMap = new Task[tasks.size()];
		
		int sum = 0;
		for(Task task : tasks) {
			addTask(task);
			if(task.getParents().isEmpty()) {
				roots.add(task);
			}
			
			sum += task.getCost();
		}
		
		totalTime = sum;
		edges = tasks.stream()
				.flatMap(t -> t.getChildren().stream())
				.collect(Collectors.toList());
		
		bottomTime = new int[topologicalOrder.size()];
		
		//Iterate backwards calculating the bottom times
		HashMap<Task, Integer> bottomMap = new HashMap<>(); //Temporary bottom time map
		for(int i = topologicalOrder.size() - 1; i >= 0; i--) {
			Task task = topologicalOrder.get(i);
			int taskBottomTime = task.getCost();
			
			for(Dependency dep : task.getChildren()) {
				Task child = dep.getTarget();
				taskBottomTime = Math.max(taskBottomTime, bottomMap.get(child) + task.getCost());
			}
			
			bottomMap.put(task, taskBottomTime);
		}
		
		buildIDs(bottomMap);
		
		for(Task task : topologicalOrder) {
			bottomTime[task.getId()] = bottomMap.get(task);
		}
	}
	
	/** Tasks with smaller bottom times should have lower Ids */
	private void buildIDs(Map<Task, Integer> bottomTimeMap) {
		int id = 0;
		
		HashMap<Task, Integer> numberOfParentsLeft = new HashMap<>();
		
		for(Task task : topologicalOrder) {
			numberOfParentsLeft.put(task, task.getParents().size());
		}
		
		 //Only when the parent has an ID can we assign an ID to the child.
		PriorityQueue<Task> freeTasks = new PriorityQueue<>((a, b) -> bottomTimeMap.get(b) - bottomTimeMap.get(a));
		freeTasks.addAll(roots);
		
		while(!freeTasks.isEmpty()) {
			Task task = freeTasks.remove();
			
			for(Dependency child : task.getChildren()) {
				if(numberOfParentsLeft.compute(child.getTarget(), (t, i) -> i - 1) == 0) {
					//No parents left
					freeTasks.add(child.getTarget());
				}
			}
			
			task.setId(id);
			taskMap[id] = task;
			
			id++;
		}
	}

	private void addTask(Task parent) {
		assert parent != null;
		
		for(Dependency dep : parent.getParents()) {
			Task task = dep.getSource();
			if(!topologicalOrder.contains(task)) {
				addTask(task);
			}
		}
		
		if(!topologicalOrder.contains(parent)) {
			topologicalOrder.add(parent);
		}
	}
	
	/** Gets the task with the given id. */
	public Task getTask(int id) {
		return taskMap[id];
	}
	
	/**
	 * Returns a List containing a topological ordering of all tasks.
	 */
	public List<Task> getAll() {
		return topologicalOrder;
	}
	
	/**
	 * Returns a Collection of all tasks with no dependents.
	 */
	public Collection<Task> getRoots() {
		return roots;
	}

	public Collection<Dependency> getEdges() {
		return edges;
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets the time from the start of this task to the end of the farthest descendant
	 */
	public int getBottomTime(Task task) {
		return bottomTime[task.getId()];
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this)
			return true;
		
		if(!(other instanceof TaskGraph)) {
			return false;
		}
		
		TaskGraph otherGraph = (TaskGraph) other;
		
		//Compare two graphs by comparing the tasks
		return name.equals(otherGraph.name) && GraphEqualityUtils.setsEqualIgnoringParents(roots, otherGraph.roots);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, topologicalOrder.size(), edges.size());
	}

	public int getTotalTaskTime() {
		return totalTime;
	}
}
