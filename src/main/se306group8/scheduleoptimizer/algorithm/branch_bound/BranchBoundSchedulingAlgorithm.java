package se306group8.scheduleoptimizer.algorithm.branch_bound;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.CriticalPathHeuristic;
import se306group8.scheduleoptimizer.algorithm.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.ListSchedule;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class BranchBoundSchedulingAlgorithm implements Algorithm {
	
	private TreeSchedule _currentSchedule;
	private TreeSchedule _bestSchedule;
	
	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		// create initial schedule where parent schedule is null and task is first task in topological order
		TreeSchedule schedule = new TreeSchedule(graph, graph.getAll().get(0), 0, new CriticalPathHeuristic());
		_bestSchedule = schedule;
		List<List<Task>> taskLists = BranchAndBound(schedule);
		return new ListSchedule(graph, taskLists);
	}

	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		// TODO Auto-generated method stub
		
	}
	
	private List<List<Task>> BranchAndBound(TreeSchedule schedule) {
		
		_currentSchedule = Branch(schedule);
		
		// check if current schedule's lower bound is lower than the best schedule
		if (_currentSchedule.compareTo(_bestSchedule) < 0) {
			_bestSchedule = _currentSchedule;
		}
		
		schedule = Bound(schedule);
		
		return _bestSchedule.computeTaskLists();
	}
	
	/** Intended to travel from a given schedule to a complete schedule using greedy logic. */
	private TreeSchedule Branch(TreeSchedule schedule) {
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder();
		// Get next schedule based on greedy logic - best lower bound is first in the list
		schedule = (greedyFinder.getChildSchedules(schedule)).get(0);
		
		// check if the schedule is a full schedule, if so, branch is complete
		// assuming this method returns null if schedule is incomplete?
		// could be good to have a method isComplete() that returns a boolean
		if (schedule.getFullSchedule() == null) {
			// Keep getting the next schedule until a full schedule is produced
			Branch(schedule);
		}
		return schedule;
	}
	
	/** Intended to return to the most recent node with multiple children and then BRANCH. */
	private TreeSchedule Bound(TreeSchedule schedule) {

		// TODO
		
		return Branch(schedule);	
	}

}
