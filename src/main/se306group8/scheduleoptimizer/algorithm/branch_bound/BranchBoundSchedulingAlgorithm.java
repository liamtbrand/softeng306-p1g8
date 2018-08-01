package se306group8.scheduleoptimizer.algorithm.branch_bound;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.ListSchedule;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class BranchBoundSchedulingAlgorithm implements Algorithm {
	
	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		// how to calculate lower bound?
		int lowerBound = 0;
		
		// create initial schedule where parent schedule is null and task is first task in topological order
		TreeSchedule schedule = new TreeSchedule(graph, null, graph.getAll().get(0), 0, lowerBound);
		List<List<Task>> taskLists = BranchAndBound(schedule);
		return new ListSchedule(graph, taskLists);
	}

	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		// TODO Auto-generated method stub
		
	}
	
	private List<List<Task>> BranchAndBound(TreeSchedule schedule) {
		
		List<List<Task>> taskLists = null;
		
		// Get best next schedule according to greedy algorithm
		// why does this method return a list of child schedules?
		// why not just return the best one? we only use the best one.
		schedule = schedule.getGreedyChildSchedules().get(0);
		if (schedule.getFullSchedule() != null) {
			taskLists = schedule.computeTaskLists();
		} else {
			BranchAndBound(schedule);
		}
		return taskLists;
	}

}
