package se306group8.scheduleoptimizer.taskgraph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskGraphBuilderTest {

	private TaskGraph smallGraph;
	
	@BeforeEach
	void createGraph() {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		builder.addTask("a", 2)
		       .addTask("b", 3)
		       .addTask("c", 3)
		       .addTask("d", 2)
		       .addDependecy("a", "b", 1)
		       .addDependecy("a", "c", 2)
		       .addDependecy("b", "d", 2)
		       .addDependecy("c", "d", 1);
		smallGraph = builder.buildGraph();
	}
	
	@Test
	void testFourNodeTaskGraph() {
			
		Iterator<Task> rootNodes =  smallGraph.getRoots().iterator();
		Task root = rootNodes.next();
		
		//Only one root node
		assertFalse(rootNodes.hasNext());
		
		assertEquals(root.getName(), "a");
		assertEquals(root.getCost(), 2);
		
		Collection<Dependency> rootChildren = root.getChildren();
		
		//Two dependencies
		assertEquals(rootChildren.size(), 2);
		
		Dependency dep = rootChildren.iterator().next();
		
		assertEquals(dep.getParent(), root);
		assertEquals(dep.getChild().getName(),"d");
		assertEquals(dep.getChild().getCost(),"1");
				
	}
	
//	@Test
//	void testTopologicalOrder() {
//		List<Task> topologicalOrder = smallGraph.getAll();
//		
//		assertEqual(topologicalOrder.get(0))
//	}
}
