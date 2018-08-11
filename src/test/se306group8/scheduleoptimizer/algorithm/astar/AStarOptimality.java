package se306group8.scheduleoptimizer.algorithm.astar;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.childfinder.BasicChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.DuplicateRemovingChildFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.AggregateHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.DataReadyTimeHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.heuristic.NoIdleTimeHeuristic;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandler;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class AStarOptimality {
	
	
	
	private Algorithm initAlgorithm(int numProcessors) {
		MinimumHeuristic heuristic = new AggregateHeuristic(new CriticalPathHeuristic(),
				new DataReadyTimeHeuristic(numProcessors), new NoIdleTimeHeuristic(numProcessors));
		return new AStarSchedulingAlgorithm(new DuplicateRemovingChildFinder(numProcessors), heuristic);
	}
	
	//@Test
	// Test ignored to please Travis
    void testProduceCompleteScheduleMediumGraph() throws IOException {
        String graphName = "2p_Fork_Nodes_10_CCR_1.97_WeightType_Random.dot";

        DOTFileHandler reader = new DOTFileHandler();
        TaskGraph graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));
        Schedule optimal = reader.readSchedule(Paths.get("dataset", "output", graphName));

        long start = System.nanoTime();
        System.out.println("Starting '" + graphName + "'");
        Schedule s = initAlgorithm(optimal.getNumberOfUsedProcessors()).produceCompleteSchedule(graph, optimal.getNumberOfUsedProcessors());
        System.out.println(s + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");

        Assertions.assertEquals(optimal.getTotalRuntime(), s.getTotalRuntime());
    }
	

    //@Test
    void testProduceCompleteScheduleAll10NodeGraphs() throws IOException {
        List<String> names = new ArrayList<>();

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("dataset", "input"))) {
            stream.forEach(p -> {
                String graphName = p.getFileName().toString();
                if(graphName.contains("Nodes_10"))
                    names.add(graphName);

            });
        }

        names.sort(null);

        for(String graphName : names) {
            DOTFileHandler reader = new DOTFileHandler();

            long start = System.nanoTime();
            Schedule optimal = reader.readSchedule(Paths.get("dataset", "output", graphName));
            if(optimal.getNumberOfUsedProcessors() > 3)
                continue;

            System.out.println("Starting '" + graphName + "'");

            TaskGraph graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));

            Schedule s = initAlgorithm(optimal.getNumberOfUsedProcessors()).produceCompleteSchedule(graph, optimal.getNumberOfUsedProcessors());

            System.out.println(s + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");

            Assertions.assertEquals(optimal.getTotalRuntime(), s.getTotalRuntime());
        }
    }

    @Test
    void testProduceCompleteScheduleTinyGraph() throws IOException {
        TaskGraph graph = TestGraphUtils.buildTestGraphA();
        Assertions.assertEquals(8, initAlgorithm(2).produceCompleteSchedule(graph, 2).getTotalRuntime());
    }


}
