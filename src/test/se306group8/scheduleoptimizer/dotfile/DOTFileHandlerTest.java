package se306group8.scheduleoptimizer.dotfile;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

class DOTFileHandlerTest {

	@Test
	void testRead() throws IOException {
		Path path = Paths.get("res", "test", "testgraphs", "a.dot");
		System.out.println(path.toAbsolutePath().toString());
	
		DOTFileHandler handler = new DOTFileHandler();
		TaskGraph graph = handler.read(path);
		
		//TODO add input validation
	}

	@Test
	void testWrite() {
		fail("Not yet implemented");
	}

}
