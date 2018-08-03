package se306group8.scheduleoptimizer.algorithm.greedy;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.TestScheduleUtils;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandler;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class TestGreedyValidity {
	
	@Test
	void testProduceCompleteScheduleAll10NodeGraphs() throws IOException {
		List<String> names = new ArrayList<>();

		try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("dataset", "input"))) {
			stream.forEach(p -> {
				String graphName = p.getFileName().toString();
				if (graphName.endsWith(".dot"))
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
			
			Schedule s = new GreedySchedulingAlgorithm().produceCompleteSchedule(graph, optimal.getNumberOfUsedProcessors());

			System.out.println(s + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");
			
			TestScheduleUtils.checkValidity(s);
		}
	}

	
	
}

