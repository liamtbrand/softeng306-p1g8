package se306group8.scheduleoptimizer.algorithm.greedy;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.algorithm.TestScheduleUtils;
import se306group8.scheduleoptimizer.dotfile.DOTFileHandler;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class TestGreedyValidity {
	
	@Test
	void testProduceCompleteScheduleAll10NodeGraphs() throws IOException, InterruptedException {
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
			
			System.out.println("Starting '" + graphName + "'");
			
			TaskGraph graph = reader.readTaskGraph(Paths.get("dataset", "input", graphName));
			
			Pattern pattern = Pattern.compile("^\\d+");
			Matcher matcher = pattern.matcher(graphName);
			matcher.find();
			String result=matcher.group(0);
			int numProcessors = Integer.parseInt(result);
			Schedule s = new GreedySchedulingAlgorithm().produceCompleteSchedule(graph, numProcessors);

			System.out.println(s + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");
			
			TestScheduleUtils.checkValidity(s,numProcessors);
		}
	}
}

