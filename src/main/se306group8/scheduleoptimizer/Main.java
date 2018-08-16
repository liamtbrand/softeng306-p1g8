package se306group8.scheduleoptimizer;

import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Application;
import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.AlgorithmFactory;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.cli.ArgsParser;
import se306group8.scheduleoptimizer.config.ArgumentException;
import se306group8.scheduleoptimizer.config.Config;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandler;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.algorithm.CLIRuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.FXApplication;

public class Main {
	
	public static volatile Config config;
	
	public static void main(String[] args) {
		
		// Parse the arguments and get a Config object.
		
		ArgsParser parser = new ArgsParser();
		
		try {
			config = parser.parse(args);
		} catch (ArgumentException e) {
			System.out.println("Problem parsing arguments: "+e.getMessage());
			System.out.println(parser.getHelp());
			return; // Stop prematurely.
		}
		
		// If we are visualising, we have to start the fx application first.
		
		if(config.visualize()) {
			Application.launch(FXApplication.class);
		} else {
			RuntimeMonitor monitor = new CLIRuntimeMonitor(config.P());
			startAlgorithm(monitor);
		}
	}

	public static void startAlgorithm(RuntimeMonitor monitor) {
		
		// Read in the task graph from the input file.
		
		DOTFileHandler fileHandler = new DOTFileHandler();
		
		TaskGraph taskGraph;
		
		try {
			taskGraph = fileHandler.readTaskGraph(Paths.get(config.inputFile()));
		} catch (IOException e) {
			System.out.println("Problem reading input file: "+e.getMessage());
			return; // Stop prematurely.
		}
		
		// Setup the Algorithm.
	
		Algorithm algorithm = AlgorithmFactory.getAlgorithm(monitor, config);
		
		// Run the Algorithm and obtain the Schedule.

		Schedule schedule;
		try {

			schedule = algorithm.produceCompleteSchedule(taskGraph, config.P());

		} catch (InterruptedException e) {
			System.out.println("Algorithm execution was interrupted.");
			return; // Stop prematurely.
		}
		
		// Write the schedule to disk.
		
		try {
			fileHandler.write(Paths.get(config.outputFile()), schedule);
		} catch (IOException e) {
			System.out.println("Problem writing output file: "+e.getMessage());
			return; // Stop prematurely.
		}
	}

}
