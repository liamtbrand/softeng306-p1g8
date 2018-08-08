package se306group8.scheduleoptimizer;

import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Application;
import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.AlgorithmFactory;
import se306group8.scheduleoptimizer.cli.ArgsParser;
import se306group8.scheduleoptimizer.config.ArgumentException;
import se306group8.scheduleoptimizer.config.Config;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandler;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.visualisation.CLIRuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.FXRuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.RuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.RuntimeMonitorAggregator;

public class Main {
	
	public static void main(String[] args) {
		
		// Parse the arguments and get a Config object.
		
		ArgsParser parser = new ArgsParser();
		Config config;
		
		try {
			config = parser.parse(args);
		} catch (ArgumentException e) {
			System.out.println("Problem parsing arguments: "+e.getMessage());
			System.out.println(parser.getHelp());
			return; // Stop prematurely.
		}
		
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
		
		AlgorithmFactory algorithmFactory
		= new AlgorithmFactory(config.P());
		
		Algorithm algorithm = algorithmFactory.getAlgorithm();
	
		// Set the runtime monitor.
		
		RuntimeMonitor cliMonitor = new CLIRuntimeMonitor(config.P());
		RuntimeMonitor fxMonitor = new FXRuntimeMonitor();
		RuntimeMonitor monitor = new RuntimeMonitorAggregator(cliMonitor, fxMonitor);
		algorithm.setMonitor(monitor);
		
		// Run the Algorithm and obtain the Schedule.
		
		Schedule schedule = algorithm.produceCompleteSchedule(taskGraph, config.P());
		
		// Write the schedule to disk.
		
		try {
			fileHandler.write(Paths.get(config.outputFile()), schedule);
		} catch (IOException e) {
			System.out.println("Problem writing output file: "+e.getMessage());
			return; // Stop prematurely.
		}
		
	}

}
