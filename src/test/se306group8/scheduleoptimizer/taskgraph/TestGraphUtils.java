package se306group8.scheduleoptimizer.taskgraph;

import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphBuilder;

/** This class creates test graphs */
public class TestGraphUtils {
	
	/** Builds the graph found in a.dot in the test graph folder. */
	public static TaskGraph buildTestGraphA() {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		
		builder.setName("Graph a");
		
		builder.addTask("a", 2);
		builder.addTask("b", 3);
		builder.addTask("c", 3);
		builder.addTask("d", 2);
		
		builder.addDependecy("a", "b", 1);
		builder.addDependecy("a", "c", 2);
		builder.addDependecy("b", "d", 2);
		builder.addDependecy("c", "d", 1);
		
		return builder.buildGraph();
	}
	
	public static TaskGraph buildTestGraphB() {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		
		builder.setName("Graph b");
		
		builder.addTask("a", 1);
		builder.addTask("b", 1);
		builder.addTask("c", 1);
				
		return builder.buildGraph();
	}
	
	public static TaskGraph buildTestGraphC() {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		
		builder.setName("Graph c");
		
		builder.addTask("a", 3);
		builder.addTask("b", 1);
		builder.addTask("c", 2);
		builder.addTask("d", 2);
		
		builder.addDependecy("b", "c", 8);
		builder.addDependecy("b", "d", 1);
				
		return builder.buildGraph();
	}
	
}
