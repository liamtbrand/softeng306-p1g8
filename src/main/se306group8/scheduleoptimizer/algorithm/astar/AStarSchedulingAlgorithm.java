package se306group8.scheduleoptimizer.algorithm.astar;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.BasicChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.storage.ScheduleStorage;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class AStarSchedulingAlgorithm implements Algorithm {
	
	private RuntimeMonitor monitor;
	
	private ScheduleStorage storage;

	@Override
	public Schedule produceCompleteSchedule(TaskGraph graph, int numberOfProcessors) {
		
		if(monitor != null) monitor.start();
		
		// Setup the schedule storage.
		
		int storageSizeLimit = 1000000; // TODO calculate properly.
		storage = new ScheduleStorage(storageSizeLimit);
		
		// Get the starting schedules and put them in storage.
		
		ChildScheduleFinder csf = new BasicChildScheduleFinder(numberOfProcessors);
		TreeSchedule schedule = new TreeSchedule(graph, (TreeSchedule s) -> 0 );
		
		while(!schedule.isComplete()) {
			storage.storeSchedules(csf.getChildSchedules(schedule));
			schedule = storage.getBestSchedule();
		}
		
		Schedule solution = schedule.getFullSchedule();
		
		if(monitor != null) monitor.finish(solution);
		
		return solution;
	}

	@Override
	public void setMonitor(RuntimeMonitor monitor) {
		this.monitor = monitor;
	}
	
}
