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
	void testRead() throws IOException {
		Path path = Paths.get("res", "test", "testgraphs", "a.dot");
		System.out.println(path.toAbsolutePath().toString());
	
		DOTFileHandler handler = new DOTFileHandler();
		TaskGraph graph = handler.read(path);
		
		Assertions.assertEquals(graph, TestGraphUtils.buildTestGraphA());
	}

	@Test
	void testWrite() throws IOException {
		Schedule output = TestScheduleUtils.createTestScheduleA();
		
		DOTFileHandler handler = new DOTFileHandler();
		
		Path tmpFolder = Files.createTempDirectory("testGraphs");
		handler.write(tmpFolder.resolve("a.dot"), output);
		
		//TODO add output validation
	}
}
