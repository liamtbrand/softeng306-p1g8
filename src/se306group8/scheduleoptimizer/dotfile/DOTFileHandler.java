package se306group8.scheduleoptimizer.dotfile;

import java.io.IOException;
import java.nio.file.Path;

import se306group8.scheduleoptimizer.algorithm.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/** This class handles the reading and writing of .dot files to and from the disk. */
public class DOTFileHandler {
	
	/**
	 * Reads a .dot file from the disk.
	 * 
	 * @param path The path to find the file at. It must not be null.
	 * @return The parsed TaskGraph, it will not be null.
	 * @throws IOException If the file cannot be read, or the file is not in the format prescribed in class.
	 */
	public TaskGraph read(Path path) throws IOException {

	}
	
	/**
	 * Writes a completed schedule to the disk. The schedule must be complete and valid.
	 * 
	 * @param path The path to write to. This must not be null. The file will be created or overwritten.
	 * @param schedule The complete schedule to write to disk. This must not be null and must be valid and complete.
	 * @throws IOException If the file cannot be written to for some reason.
	 */
	public void write(Path path, Schedule schedule) throws IOException {
		
	}
}
