package se306group8.scheduleoptimizer.dotfile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import se306group8.scheduleoptimizer.algorithm.ListSchedule;
import se306group8.scheduleoptimizer.dotfile.Token.Type;
import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TaskGraphBuilder;

/** This class handles the reading and writing of .dot files to and from the disk.
 * 
 * <a>https://www.graphviz.org/doc/info/lang.html</a> */
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
	public TaskGraph readTaskGraph(Path path) throws IOException {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		
		read(path, builder, null);
		
		return builder.buildGraph();
	}

	/**
	 * Reads a .dot file from the disk.
	 * 
	 * @param path The path to find the file at. It must not be null.
	 * @return The parsed Schedule, it will not be null.
	 * @throws IOException If the file cannot be read, or the file is not in the format prescribed in class.
	 */
	public Schedule readSchedule(Path path) throws IOException {
		TaskGraphBuilder builder = new TaskGraphBuilder();
		TreeMap<Integer, TreeMap<Integer, String>> processorMapping = new TreeMap<>();
		
		//Read populates the processorMapping and the builder
		read(path, builder, processorMapping);
		
		TaskGraph graph = builder.buildGraph();
		HashMap<String, Task> taskMapping = new HashMap<>();
		
		//Assign each name to a task
		for(Task t : graph.getAll()) {
			taskMapping.put(t.getName(), t);
		}
		
		List<List<Task>> processorAllocation = new ArrayList<>();
		
		for(TreeMap<Integer, String> map : processorMapping.values()) {
			ArrayList<Task> list = new ArrayList<>();
			processorAllocation.add(list);
			
			for(String taskName : map.values()) {
				list.add(taskMapping.get(taskName));
			}
		}
		
		return new ListSchedule(graph, processorAllocation);
	}
	
	/**
	 * Internal method to read a dot file from a map into a designated builder.
	 * Mapping may be null, in which case processor information is ignored.
	 * <br>
	 * Mapping is chosen with this structure so that the tasks are ordered by processor and then by start-time, to aid graph construction.
	 * 
	 * @param path The path to a valid taskgraph file, or schedule
	 * @param builder A TaskGraph builder. Must not be null
	 * @param mapping A TreeMap storing the mapping from processor to a mapping from start-time to task name.
	 * @throws IOException
	 */
	private void read(Path path, TaskGraphBuilder builder, TreeMap<Integer, TreeMap<Integer, String>> mapping) throws IOException {
		List<Token> tokens = new ArrayList<>();
		
		try(PushbackReader reader = new PushbackReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
			Token token;
			do {
				token = Token.readNextToken(reader);
				if(token.type != Type.IGNORED)
					tokens.add(token);
			} while(token.type != Type.EOF);
		}
		
		try {
			tokens = mergeQuotes(tokens);
			
			parseTokens(tokens.listIterator(), builder, mapping);
		} catch(NoSuchElementException nsee) {
			throw new IOException("Expected token", nsee);
		}
	}
	
	/** Merges sets of quotes separated by + symbols into a single quote. 
	 * @throws IOException */
	private List<Token> mergeQuotes(List<Token> tokens) throws IOException {
		List<Token> result = new ArrayList<>();
		
		ListIterator<Token> iterator = tokens.listIterator();
		while(iterator.hasNext()) {
			Token token = iterator.next();
			if(token.type == Type.QUOTE) {
				result.add(tryToMergeQuotes(iterator));
			} else {
				result.add(token);
			}
		}
		
		return result;
	}

	/** Attempts to merge a group of quotes starting at the position of this iterator. 
	 * @throws IOException */
	private Token tryToMergeQuotes(ListIterator<Token> iterator) throws IOException {
		iterator.previous();
		
		//We are now at the start of the quote.
		String mergedString = "";
		
		while(true) {
			Token quote = iterator.next();
			if(quote.type != Type.QUOTE) {
				throw new IOException("All + tokens must be preceded and followed by a quoted string.");
			}
			
			mergedString += quote.value;
			
			if(!iterator.hasNext()) { //No more pluses, return the merged String.
				return Token.createQuoteToken(mergedString);
			}
			
			Token plus = iterator.next();
			if(plus.type != Type.CONTROL_CHAR || !plus.value.equals("+")) {
				//Not a plus, roll-back and then return
				iterator.previous();
				return Token.createQuoteToken(mergedString);
			}
		}
	}

	/** Takes a list of tokens and populates the builder and the mapping. Very similar to {@link DOTFileHandler#read(Path, TaskGraphBuilder, TreeMap)} */
	private void parseTokens(ListIterator<Token> iter, TaskGraphBuilder builder, TreeMap<Integer, TreeMap<Integer, String>> processorMapping) throws IOException {
		//Read the header
		Token graphType = iter.next();
		if(!graphType.value.toLowerCase().equals("digraph"))
			throw new IOException("Only digraphs are supported.");
		
		//Read the name
		Token next = iter.next();
		if(next.type == Type.ID || next.type == Type.QUOTE) {
			builder.setName(next.value);
		} else if(next.value.equals("{")) {
			builder.setName("");
		} else {
			throw new IOException("Expected ID or QUOTE or {");
		}
		
		//Read the rest
		readStatementList(iter, builder, processorMapping);
		
		if(iter.next().type != Token.Type.EOF) {
			throw new IOException("There can be no tokens after the closing brace.");
		}
	}

	/** Reads the list of statements, that may or may not be separated by semi-colons. If processorMapping is not null
	 * it will be populated with the start time and processor allocations. */
	private void readStatementList(ListIterator<Token> iter, TaskGraphBuilder builder, TreeMap<Integer, TreeMap<Integer, String>> processorMapping) throws IOException {
		//Ignore the opening {
		iter.next();
		
		Token t;
		while(!(t = iter.next()).value.equals("}")) {
			if(t.type == Type.KEYWORD) {
				//Ignore attributes for a attr_stmt
				new Attributes(iter);
			} else {
				String itemName = t.getID();
				
				if(iter.next().type == Type.EDGE_OP) {
					//Read edge statement
					
					Token other = iter.next();
					
					if(other.value.equals("{")) {
						throw new IOException("Clusters are not supported");
					}
					
					String otherItem = other.getID();
					Attributes attr = new Attributes(iter);
					
					builder.addDependecy(itemName, otherItem, attr.edgeWeight);
				} else {
					//Read node statement
					iter.previous();
					
					Attributes attr = new Attributes(iter);
					builder.addTask(itemName, attr.nodeWeight);
					
					if(processorMapping != null && attr.processor != -1 && attr.startTime != -1) {
						processorMapping.computeIfAbsent(attr.processor, i -> new TreeMap<Integer, String>()).put(attr.startTime, itemName);
					}
				}
			}
			
			if(iter.next().value.equals(";")) {
				//Ignore ;
			} else {
				iter.previous();
			}
		}
	}

	private class Attributes {
		/** iter.next should return [ when the function returns next will return the token after ] */
		Attributes(ListIterator<Token> iter) throws IOException {
			if(iter.next().value.equals("[")) {
				//Ignore the [
			} else {
				iter.previous();
				return;
			}
			
			while(true) {
				if(iter.next().value.equals("]")) {
					if(!iter.next().value.equals("[")) {
						//We ended this attributes set
						iter.previous(); //rollback
						return;
					}
					
					continue;
				} else {
					//Rollback the check
					iter.previous();
				}
				
				addAttribute(iter);
				
				Token next = iter.next();
				if(next.value.equals(";") || next.value.equals(",")) {
					//Ignore separators
				} else {
					iter.previous();
				}
			}
		}
		
		private void addAttribute(ListIterator<Token> iter) throws IOException {
			Token name = iter.next();
			
			Token equals = iter.next();
			if(!equals.value.equals("="))
				throw new IOException("Invalid attribute specification");
			
			Token value = iter.next();
			
			try {
				addAttribute(name.getID(), value.getID());
			} catch(IOException unused) { /* Ignore malformed attribute values. */ }
		}
		
		private void addAttribute(String attr, String value) throws IOException {
			int number;
			try {
				number = Integer.parseInt(value);
			} catch(NumberFormatException nfe) {
				throw new IOException("Unexpected non-integer attribute");
			}
			
			if(attr.equals(nodeWeightAttribute)) {
				nodeWeight = number;
			}
			
			if(attr.equals(edgeWeightAttribute)) {
				edgeWeight = number;
			}
			
			if(attr.equals(startTimeAttribute)) {
				startTime = number;
			}

			if(attr.equals(processorAttribute)) {
				processor = number;
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

		try (BufferedWriter reader = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			reader.write(output.toString());
		}
	}
}
