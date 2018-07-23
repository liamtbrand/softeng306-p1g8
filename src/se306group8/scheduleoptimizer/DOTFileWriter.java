package se306group8.scheduleoptimizer;
import java.io.IOException;
import java.nio.file.Path;

public interface DOTFileWriter {

	public void write( Path path, TaskGraph graph, Schedule schedule ) throws IOException;
	
}
