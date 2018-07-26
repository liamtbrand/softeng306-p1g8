package se306group8.scheduleoptimizer.dotfile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

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
	public TaskGraph read(Path path) throws IOException {
		List<Token> tokens = new ArrayList<>();
		TaskGraphBuilder builder = new TaskGraphBuilder();
		
		try(PushbackReader reader = new PushbackReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
			Token token;
			do {
				token = readToken(reader);
				if(token.type != Type.IGNORED)
					tokens.add(token);
			} while(token.type != Type.EOF);
		}
		
		try {
			return parseTokens(tokens.listIterator(), builder);
		} catch(NoSuchElementException nsee) {
			throw new IOException("Expected token", nsee);
		}
	}
	
	/** Takes a list of tokens and parses it into a graph. */
	private TaskGraph parseTokens(ListIterator<Token> iter, TaskGraphBuilder builder) throws IOException {
		//Read the header
		Token graphType = iter.next();
		if(!graphType.value.toLowerCase().equals("digraph"))
			throw new IOException("Only digraphs are supported.");
		
		Token next = iter.next();
		if(next.type == Type.QUOTE || next.type == Type.ID) {
			builder.setName(next.getID());
		} else if(next.value.equals("{")) {
			builder.setName("");
		} else {
			throw new IOException("Expected ID or QUOTE or {");
		}
		
		readStatementList(iter, builder);
		
		return builder.buildGraph();
	}

	/** Reads the list of statements, that may or may not be separated by semi-colons */
	private void readStatementList(ListIterator<Token> iter, TaskGraphBuilder builder) throws IOException {
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
					String otherItem = iter.next().getID();
					Attributes attr = new Attributes(iter);
					
					builder.addDependecy(itemName, otherItem, attr.edgeWeight);
				} else {
					iter.previous();
					
					Attributes attr = new Attributes(iter);
					builder.addTask(itemName, attr.nodeWeight);
				}
			}
			
			if(iter.next().value.equals(";")) {
				//Ignore
			} else {
				iter.previous();
			}
		}
	}

	/** A token is a single coherent unit, such as a comment, [, ], ;, graph, ->, ect... */
	private Token readToken(PushbackReader reader) throws IOException {
		int c;
		while((c = reader.read()) != -1) {
			if(c == '\r' || c == '\n') {
				int peek = reader.read();
				if(peek == -1)
					return Token.EOF();
				
				if(peek == '#') {
					do {
						c = reader.read();
					} while(c != '\r' && c != '\n' && c != -1);
					continue;
				} else {
					reader.unread(peek);
				}
			}
			
			if(Character.isWhitespace(c))
				continue;
			
			//Read single characters
			switch(c) {
			case '{':
			case '}':
			case ']':
			case '[':
			case ',':
			case ';':
			case ':':
			case '=':
				return new Token(c);
			case '"':
				return Token.quote(reader);
			case '-':
				return Token.edgeOp(reader);
			case '/': //There is no valid / symbol in the syntax, either it is a comment of an error
				return Token.comment(reader);
			}
			
			//We are now reading ID or KEYWORD
			reader.unread(c);
			return Token.readIDOrKeyword(reader);
		}
		
		return Token.EOF();
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
			
			addAttribute(name.getID(), value.getID());
		}
		
		private void addAttribute(String attr, String value) throws IOException {
			int number = Integer.parseInt(value);
			
			if(attr.equals(nodeWeightAttribute)) {
				nodeWeight = number;
			} else if(attr.equals(edgeWeightAttribute)) {
				edgeWeight = number;
			} else if(attr.equals(startTimeAttribute)) {
				startTime = number;
			} else if(attr.equals(processorAttribute)) {
				processor = number;
			} else {
				throw new IOException("Unknown attribute");
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

		try (BufferedWriter reader = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
			reader.write(output.toString());
		}
	}
}
