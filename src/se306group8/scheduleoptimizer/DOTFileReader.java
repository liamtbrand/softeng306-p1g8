package se306group8.scheduleoptimizer;
import java.io.IOException;
import java.nio.file.Path;

public interface DOTFileReader {
	
	TaskGraph read( Path path ) throws IOException;

}
