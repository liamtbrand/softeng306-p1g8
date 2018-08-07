package se306group8.scheduleoptimizer.dotfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.ListSchedule;
import se306group8.scheduleoptimizer.algorithm.TestScheduleUtils;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphBuilder;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

/** Tests the DOT file IO by reading and writing several files from disk. The writer is checked by reading the file back in. */
class DOTFileHandlerTest {

	@Test
	void testReadValidation() throws IOException {
		Path path = Paths.get("res", "test", "testgraphs", "a.dot");
		
		DOTFileHandler handler = new DOTFileHandler();
		TaskGraph graph = handler.readTaskGraph(path);
		
		Assertions.assertEquals(graph, TestGraphUtils.buildTestGraphA());
	}
	
	@Test
	void testReadCoverage() throws IOException {
		Path path = Paths.get("res", "test", "testgraphs", "graph_with_coverage.dot");
		
		DOTFileHandler handler = new DOTFileHandler();
		Assertions.assertEquals(handler.readTaskGraph(path), TestGraphUtils.buildTestGraphA());
	}

	@Test
	void testReadInputDataset() throws IOException {
		DOTFileHandler handler = new DOTFileHandler();
		for(Path p : Files.newDirectoryStream(Paths.get("dataset", "input"), "*.dot")) {
			try {
				handler.readTaskGraph(p);
			} catch(IOException ioex) {
				Assertions.fail("Failed to read: " + p, ioex);
			}
		}
		
		for(Path p : Files.newDirectoryStream(Paths.get("dataset", "canvasinput"), "*.dot")) {
			try {
				handler.readTaskGraph(p);
			} catch(IOException ioex) {
				Assertions.fail("Failed to read: " + p, ioex);
			}
		}
	}
	
	@Test
	void testReadSchedule() throws IOException {
		DOTFileHandler handler = new DOTFileHandler();
		
		Path schedule = Paths.get("res", "test", "testgraphs", "a_schedule.dot");
		
		Assertions.assertEquals(TestScheduleUtils.createTestScheduleA(), handler.readSchedule(schedule));
	}
	
	/** This is a regression test for the double writing of the graph to the same file. */
	@Test
	void testTwiceWrite() throws IOException {
		TaskGraph graph = new TaskGraphBuilder()
				.addTask("a", 1)
				.addTask("b", 2)
				.addTask("c", 1)
				.addTask("d", 2)
				.addTask("e", 1)
				.addTask("ea", 1)
				.addTask("eb", 1)
				.addTask("ec", 1)
				.addTask("ed", 1)
				.addTask("ee", 1)
				.addTask("ef", 1)
				.addTask("eg", 1)
				.addTask("eh", 1)
				.addTask("ei", 1)
				.addTask("ej", 1)
				.addTask("ek", 1)
				.addTask("el", 1)
				.addTask("em", 1)
				.addTask("en", 1)
				.addTask("eo", 1)
				.addTask("ep", 1)
				.addTask("eq", 1)
				.addTask("er", 1)
				.addTask("es", 1)
				.addTask("et", 1)
				.addTask("f", 3).buildGraph();
		
		ListSchedule schedule = new ListSchedule(graph, Arrays.asList(graph.getAll()));
		
		Path tmpFolder = Files.createTempDirectory("testGraphs");
		DOTFileHandler handler = new DOTFileHandler();
		
		handler.write(tmpFolder.resolve("double-write.dot"), schedule, schedule.getGraph().getName());
		
		Schedule testSchedule = TestScheduleUtils.createTestScheduleA();
		handler.write(tmpFolder.resolve("double-write.dot"), testSchedule, testSchedule.getGraph().getName());
		
		Assertions.assertEquals(TestScheduleUtils.createTestScheduleA(), handler.readSchedule(tmpFolder.resolve("double-write.dot")));
	}
	
	@Test
	void testWrite() throws IOException {
		Schedule output = TestScheduleUtils.createTestScheduleA();
		
		DOTFileHandler handler = new DOTFileHandler();
		
		Path tmpFolder = Files.createTempDirectory("testGraphs");
		handler.write(tmpFolder.resolve("a.dot"), output, output.getGraph().getName());
		
		Assertions.assertEquals(handler.readSchedule(tmpFolder.resolve("a.dot")), TestScheduleUtils.createTestScheduleA());
	}
}
