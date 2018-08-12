package se306group8.scheduleoptimizer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.astar.AStarSchedulingAlgorithm;
import se306group8.scheduleoptimizer.algorithm.branchbound.BranchBoundSchedulingAlgorithm;
import se306group8.scheduleoptimizer.algorithm.childfinder.BasicChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.DuplicateRemovingChildFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.AggregateHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.DataReadyTimeHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.NoIdleTimeHeuristic;
import se306group8.scheduleoptimizer.algorithm.storage.BlockScheduleStorage;
import se306group8.scheduleoptimizer.algorithm.storage.NonPruningScheduleStorage;
import se306group8.scheduleoptimizer.algorithm.storage.ObjectQueueScheduleStorage;
import se306group8.scheduleoptimizer.algorithm.storage.ScheduleStorage;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandler;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

/** This class is a testing utility that runs the chosen algorithm and writes out various metrics. */
public class PerformanceTest {
	private static final Map<String, Predicate<String>> DIFFICULTIES = new HashMap<>();
	private static final Map<String, AlgorithmConstructor> ALGORITHMS = new HashMap<>();
	private static final Map<String, IntFunction<MinimumHeuristic>> HEURISTICS = new HashMap<>();
	private static final Map<String, IntFunction<ChildScheduleFinder>> CHILD_FINDER = new HashMap<>();
	private static final Map<String, Supplier<ScheduleStorage>> STORAGE = new HashMap<>();

	@FunctionalInterface
	private static interface AlgorithmConstructor {
		Algorithm construct(MinimumHeuristic heuristic, ChildScheduleFinder finder, RuntimeMonitor monitor, ScheduleStorage storage);
	}

	//Set up the various testing options.
	static {
		DIFFICULTIES.put("EASY", s -> s.contains("Nodes_1") && s.startsWith("2p"));
		DIFFICULTIES.put("MEDIUM", s -> !s.contains("Nodes_3") && !s.matches(".*(Fork_Join_Nodes_21|Fork_Nodes_21|Join_Nodes_21).*"));
		DIFFICULTIES.put("HARD", s -> true);

		ALGORITHMS.put("A_STAR", (h, f, m, s) -> new AStarSchedulingAlgorithm(f, h, m, s));
		ALGORITHMS.put("BRANCH_BOUND", (h, f, m, s) -> new BranchBoundSchedulingAlgorithm(f, h, m));

		HEURISTICS.put("ZERO", processors -> schedule -> 0);
		HEURISTICS.put("NO_IDLE", NoIdleTimeHeuristic::new);
		HEURISTICS.put("CRITICAL_PATH", processors -> new CriticalPathHeuristic());
		HEURISTICS.put("DATA_READY_TIME", DataReadyTimeHeuristic::new);

		CHILD_FINDER.put("BASIC", BasicChildScheduleFinder::new);
		CHILD_FINDER.put("GREEDY", GreedyChildScheduleFinder::new);
		CHILD_FINDER.put("DUPLICATE_REMOVING", DuplicateRemovingChildFinder::new);

		STORAGE.put("PRUNING", BlockScheduleStorage::new);
		STORAGE.put("NON_PRUNING", NonPruningScheduleStorage::new);
		STORAGE.put("OBJECT_QUEUE", ObjectQueueScheduleStorage::new);
	}

	//Run through the data-set, creating a mapping from the file name to the length of time, and the number of solutions stored
	public static void main(String[] args) throws ParseException, IOException {
		Options options = new Options();

		options.addOption("d", true, "The difficulty, EASY, MEDIUM or HARD");
		options.addOption("a", true, "The algorithm to use, A_STAR, BRANCH_BOUND");
		options.addOption("h", true, "The heuristic to use, as a comma seperated list of heuristics, ZERO, NO_IDLE, CRITICAL_PATH, DATA_READY_TIME");
		options.addOption("c", true, "The child finder to use, BASIC, GREEDY, DUPLICATE_REMOVING");
		options.addOption("n", true, "The number of files to process.");
		options.addOption("s", true, "The storage system to use, only used with A*, ignored with anything else.");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		String difficulty, heuristicOptionString, childFinder, algorithm, storage;

		difficulty = cmd.getOptionValue("d", "EASY");
		heuristicOptionString = cmd.getOptionValue("h", "CRITICAL_PATH,NO_IDLE,DATA_READY_TIME");
		childFinder = cmd.getOptionValue("c", "BASIC");
		algorithm = cmd.getOptionValue("a", "A_STAR");
		storage = cmd.getOptionValue("s", "PRUNING");

		int n = Integer.parseInt(cmd.getOptionValue("n", "10"));

		List<IntFunction<MinimumHeuristic>> heuristicBuilders = Arrays.stream(heuristicOptionString.split(","))
				.map(HEURISTICS::get)
				.collect(Collectors.toList());

		IntFunction<MinimumHeuristic> heuristicBuilder;
		if(heuristicBuilders.size() == 1) {
			heuristicBuilder = heuristicBuilders.get(0);
		} else {
			heuristicBuilder = processors -> {
				MinimumHeuristic[] heuristics = heuristicBuilders.stream().map(builder -> builder.apply(processors)).toArray(MinimumHeuristic[]::new);
				return new AggregateHeuristic(heuristics);
			};
		}

		//This is the file name, made out of the command options.
		String fileName = difficulty + "-" + heuristicOptionString + "-" + childFinder + "-" + algorithm + "-" + storage + "-" + (n == -1 ? "NOLIMIT" : Integer.toString(n));
		System.out.println("Starting test '" + fileName + "'");

		Predicate<String> inputFilter = DIFFICULTIES.get(difficulty);
		IntFunction<ChildScheduleFinder> childScheduleBuilder = CHILD_FINDER.get(childFinder);
		AlgorithmConstructor algorithmConstructor = ALGORITHMS.get(algorithm);
		Supplier<ScheduleStorage> storageSupplier = STORAGE.get(storage);

		List<String> names = new ArrayList<>();

		//Read the list of file names
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("dataset", "input"))) {
			stream.forEach(p -> {
				String graphName = p.getFileName().toString();
				if(inputFilter.test(graphName))
					names.add(graphName);
			});
		}

		names.sort(null);
		Pattern numberExtraction = Pattern.compile("^(\\d+)");

		//Create the file
		Files.createDirectories(Paths.get("performance"));
		BufferedWriter output = Files.newBufferedWriter(Paths.get("performance", fileName + ".csv"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		int i = 0;

		//Write the header
		output.write("\"Graph Name\",\"Optimal\",\"Time Taken (ms)\",\"Solutions Searched\"\n");

		try {
			for(String graphName : names) {
				if(i == n) {
					break;
				}

				i++;

				System.out.println("Starting Test " + i + "/" + (n == -1 ? names.size() : n) + " " + graphName);

				DOTFileHandler reader = new DOTFileHandler();

				Schedule optimal = reader.readSchedule(Paths.get("dataset", "output", graphName));
				TaskGraph graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));

				int processors;
				Matcher m = numberExtraction.matcher(graphName);
				if(!m.matches()) {
					processors = optimal.getNumberOfUsedProcessors();
				} else {
					processors = Integer.parseInt(m.group(1));
				}

				RuntimeMonitor monitor = new RuntimeMonitor() {
					int millionSolutions;
					int numberOfSolutions;
					long startTime;

					@Override
					public void updateBestSchedule(TreeSchedule optimalSchedule) {}

					@Override
					public void start() {
						millionSolutions = 0;
						startTime = System.nanoTime();
					}

					@Override
					public void logMessage(String message) {
						System.out.println("Msg: " + message + "\n");
					}

					@Override
					public void setSolutionsExplored(int number) {
						numberOfSolutions = number;

						if(number / 1_000_000 > millionSolutions) {
							millionSolutions = number / 1_000_000;
							System.out.println(millionSolutions + "M solutions");
						}
					}

					@Override
					public void finish(Schedule solution) {
						long timeTaken = System.nanoTime() - startTime;

						try {
							output.write("\"" + graphName + "\",\"" + (solution.getTotalRuntime() == optimal.getTotalRuntime()) + "\",\"" + timeTaken / 1_000_000 + "\",\"" + numberOfSolutions + "\"\n");
						} catch (IOException e) { throw new RuntimeException(e); }
					}
				};

				//Create the algorithm objects
				MinimumHeuristic heuristic = heuristicBuilder.apply(processors);
				ChildScheduleFinder child = childScheduleBuilder.apply(processors);
				ScheduleStorage scheduleStorage = storageSupplier.get();
				Algorithm alg = algorithmConstructor.construct(heuristic, child, monitor, scheduleStorage);

				Schedule s = alg.produceCompleteSchedule(graph, processors);

				if(s.getTotalRuntime() != optimal.getTotalRuntime()) {
					System.out.println("Failed to find the optimal solution");
				}
			}
		} catch(OutOfMemoryError oome) {
			output.write("\"Out of memory\"\n");
		} finally {
			output.close();
		}
	}
}
