package se306group8.scheduleoptimizer.dotfile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphBuilder;

/** This class handles the reading and writing of .dot files to and from the disk. */
public class DOTFileHandler {
	private final String nodeWeightAttribute;
	private final String edgeWeightAttribute;
	private final String startTimeAttribute;
	private final String processorAttribute;
	
	/** Creates a file reader with the default parameters. */
	public DOTFileHandler() {
		nodeWeightAttribute = "Weight";
		edgeWeightAttribute = "Weight";
		startTimeAttribute = "Start";
		processorAttribute = "Processor";
	}
	
	/**
	 * Reads a .dot file from the disk.
	 * 
	 * @param path The path to find the file at. It must not be null.
	 * @return The parsed TaskGraph, it will not be null.
	 * @throws IOException If the file cannot be read, or the file is not in the format prescribed in class.
	 */
	public TaskGraph read(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		TaskGraphBuilder builder = new TaskGraphBuilder();
		
		Iterator<String> iterator = lines.iterator();
		
		String headerLine = iterator.next();
		
		parseHeader(headerLine, builder);
		
		String tmp;
		while(iterator.hasNext() && !(tmp = iterator.next().trim()).equals("}")) {
			if(!tmp.isEmpty())
				parseLine(tmp, builder);
		}
		
		return builder.buildGraph();
	}
	
	private class Attributes {
		Attributes(String input) throws IOException {
			String[] attributes = input.split(",");
			for(String entry : attributes) {
				String[] items = entry.split("=");
				
				String name;
				int number;
				
				try {
					name = items[0];
					number = Integer.parseInt(items[1]);
				} catch(ArrayIndexOutOfBoundsException aioobe) {
					throw new IOException();
				}
				
				if(name.equals(nodeWeightAttribute)) {
					nodeWeight = number;
				} else if(name.equals(edgeWeightAttribute)) {
					edgeWeight = number;
				} else if(name.equals(startTimeAttribute)) {
					startTime = number;
				} else if(name.equals(processorAttribute)) {
					processor = number;
				} else {
					throw new IOException("Unknown attribute");
				}
			}
		}
		
		public Attributes(Task task, int processorNumber, int startTime) {
			this.startTime = startTime;
			this.processor = processorNumber;
			this.nodeWeight = task.getCost();
		}

		public Attributes(Dependency edge) {
			edgeWeight = edge.getCommunicationCost();
		}

		int nodeWeight = -1;
		int edgeWeight = -1;
		int startTime = -1;
		int processor = -1;
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			
			builder.append('[');
			
			boolean isNotFirstAttribute = false;
			
			if(nodeWeight != -1) {
				builder.append(nodeWeightAttribute).append("=").append(Integer.toString(nodeWeight));
				isNotFirstAttribute = true;
			}
			
			if(edgeWeight != -1) {
				if(isNotFirstAttribute)
					builder.append(',');
				
				builder.append(edgeWeightAttribute).append("=").append(Integer.toString(edgeWeight));
				isNotFirstAttribute = true;
			}
			
			if(processor != -1) {
				if(isNotFirstAttribute)
					builder.append(',');
				
				builder.append(processorAttribute).append("=").append(Integer.toString(processor));
				isNotFirstAttribute = true;
			}
			
			if(startTime != -1) {
				if(isNotFirstAttribute)
					builder.append(',');
				
				builder.append(startTimeAttribute).append("=").append(Integer.toString(startTime));
				isNotFirstAttribute = true;
			}
			
			builder.append(']');
			
			return builder.toString();
		}
	}
	
	private void parseLine(String line, TaskGraphBuilder builder) throws IOException {
		line = line.replaceAll("\\s", "");
		Matcher nodePattern = Pattern.compile("(\\w+)\\[(.*)\\];").matcher(line);
		Matcher edgePattern = Pattern.compile("(\\w+)->(\\w+)\\[(.*)\\];").matcher(line);
	
		if(nodePattern.matches()) {
			String node = nodePattern.group(1);
			Attributes attr = new Attributes(nodePattern.group(2));
			
			builder.addTask(node, attr.nodeWeight);
		} else if(edgePattern.matches()) {
			String parent = edgePattern.group(1);
			String child = edgePattern.group(2);
			Attributes attr = new Attributes(edgePattern.group(3));
			
			builder.addDependecy(parent, child, attr.edgeWeight);
		} else {
			throw new IOException("Malformed line: " + line);
		}
	}

	private void parseHeader(String headerLine, TaskGraphBuilder builder) throws IOException {
		Matcher extract = Pattern.compile("\"(.*)\"").matcher(headerLine);
		if(!extract.find()) {
			throw new IOException("Unamed graph: " + headerLine);
		}
		
		builder.setName(extract.group(1));
	}

	/**
	 * Writes a completed schedule to the disk. The schedule must be complete and valid.
	 * 
	 * @param path The path to write to. This must not be null. The file will be created or overwritten.
	 * @param schedule The complete schedule to write to disk. This must not be null and must be valid and complete.
	 * @throws IOException If the file cannot be written to for some reason.
	 */
	public void write(Path path, Schedule schedule) throws IOException {
		StringBuilder output = new StringBuilder();
		
		TaskGraph graph = schedule.getGraph();
		
		output.append("digraph \"").append(graph.getName()).append("\" {").append(System.lineSeparator());
		
		for(Task task : graph.getAll()) {
			Attributes attr = new Attributes(task, schedule.getProcessorNumber(task), schedule.getStartTime(task));
			
			output
			.append('\t').append(task.getName())
			.append('\t').append(attr.toString()).append(";").append(System.lineSeparator());
		}
		
		for(Dependency edge : graph.getEdges()) {
			Attributes attr = new Attributes(edge);
			
			output
			.append('\t').append(edge.getSource().getName()).append(" -> ").append(edge.getTarget().getName())
			.append('\t').append(attr.toString()).append(";").append(System.lineSeparator());
		}
		
		output.append("}").append(System.lineSeparator());
		
		try (BufferedWriter reader = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
			reader.write(output.toString());
		}
	}
}
