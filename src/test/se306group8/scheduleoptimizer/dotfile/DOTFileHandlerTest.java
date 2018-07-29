package se306group8.scheduleoptimizer.dotfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

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
	
	@Test
	void testWrite() throws IOException {
		Schedule output = TestScheduleUtils.createTestScheduleA();
		
		DOTFileHandler handler = new DOTFileHandler();
		
		Path tmpFolder = Files.createTempDirectory("testGraphs");
		handler.write(tmpFolder.resolve("a.dot"), output);
		
		Assertions.assertEquals(handler.readSchedule(tmpFolder.resolve("a.dot")), TestScheduleUtils.createTestScheduleA());
	}
}
