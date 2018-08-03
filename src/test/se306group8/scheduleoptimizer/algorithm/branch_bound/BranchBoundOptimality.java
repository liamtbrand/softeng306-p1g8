package se306group8.scheduleoptimizer.algorithm.branch_bound;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.dotfile.DOTFileHandler;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;
import se306group8.scheduleoptimizer.taskgraph.TestGraphUtils;

public class BranchBoundOptimality {
	
	@Test
    void testProduceCompleteScheduleMediumGraph() throws IOException {
        String graphName = "2p_Fork_Nodes_10_CCR_1.97_WeightType_Random.dot";

        DOTFileHandler reader = new DOTFileHandler();
        TaskGraph graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));
        Schedule optimal = reader.readSchedule(Paths.get("dataset", "output", graphName));

        long start = System.nanoTime();
        System.out.println("Starting '" + graphName + "'");
        Schedule s = new BranchBoundSchedulingAlgorithm().produceCompleteSchedule(graph, optimal.getNumberOfUsedProcessors());
        System.out.println(s + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");

        Assertions.assertEquals(optimal.getTotalRuntime(), s.getTotalRuntime());
    }
	
//
//    @Test
//    void testProduceCompleteScheduleAll10NodeGraphs() throws IOException {
//        List<String> names = new ArrayList<>();
//
//        try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("dataset", "input"))) {
//            stream.forEach(p -> {
//                String graphName = p.getFileName().toString();
//                if(graphName.contains("Nodes_10"))
//                    names.add(graphName);
//
//            });
//        }
//
//        names.sort(null);
//
//        for(String graphName : names) {
//            DOTFileHandler reader = new DOTFileHandler();
//
//            long start = System.nanoTime();
//            Schedule optimal = reader.readSchedule(Paths.get("dataset", "output", graphName));
//            if(optimal.getNumberOfUsedProcessors() > 3)
//                continue;
//
//            System.out.println("Starting '" + graphName + "'");
//
//            TaskGraph graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));
//
//            Schedule s = new BranchBoundSchedulingAlgorithm().produceCompleteSchedule(graph, optimal.getNumberOfUsedProcessors());
//
//            System.out.println(s + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");
//
//            Assertions.assertEquals(optimal.getTotalRuntime(), s.getTotalRuntime());
//        }
//    }
//
//    @Test
//    void testProduceCompleteScheduleTinyGraph() throws IOException {
//        TaskGraph graph = TestGraphUtils.buildTestGraphA();
//        Assertions.assertEquals(8, new BranchBoundSchedulingAlgorithm().produceCompleteSchedule(graph, 2).getTotalRuntime());
//    }


}
