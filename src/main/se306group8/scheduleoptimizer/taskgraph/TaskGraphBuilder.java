package se306group8.scheduleoptimizer.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @brief Create an object representation of a task graph.
 *
 */
public class TaskGraphBuilder {
	private Map<String,Task> taskMap;
	private Map<String, Collection<Dependency>> parentMap;
	private Map<String, Collection<Dependency>> childMap;
	private String name;
	
	public TaskGraphBuilder(){
		taskMap = new HashMap<String,Task>();
		parentMap = new HashMap<String, Collection<Dependency>>();
		childMap = new HashMap<String, Collection<Dependency>>();
	}
	
	public TaskGraphBuilder addTask(String name, int cost){
		taskMap.put(name, new Task(name,cost));
		parentMap.put(name, new ArrayList<Dependency>());
		childMap.put(name, new ArrayList<Dependency>());
		return this;
		
	}
	
	public TaskGraphBuilder addDependecy(String sourceTaskName, String targetTaskName, int cost){
		Dependency dep = new Dependency(taskMap.get(sourceTaskName),taskMap.get(targetTaskName), cost);
		parentMap.get(targetTaskName).add(dep);
		childMap.get(sourceTaskName).add(dep);
		return this;
	}
	
	public TaskGraph buildGraph(){
		Set<String> taskNames = taskMap.keySet();
		for (String task:taskNames){
			Task t = taskMap.get(task);
			t.setChildDependencies(childMap.get(task));
			t.setParentDependencies(parentMap.get(task));
		}
		return new TaskGraph(name, taskMap.values());
		
	}

	public void setName(String name) {
		this.name = name;
	}

}
