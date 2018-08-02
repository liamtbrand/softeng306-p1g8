package se306group8.scheduleoptimizer.algorithm.heuristic;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;

public class NoIdleTimeHeuristic implements MinimumHeuristic {
	private final int processors;
	
	public NoIdleTimeHeuristic(int processors) {
		this.processors = processors;
	}
	
	@Override
	public int estimate(TreeSchedule schedule) {
		int idleTime = schedule.getIdleTime();
		int totalTime = schedule.getGraph().getAll().stream().mapToInt(task -> task.getCost()).sum();
		
		//Ceil div
		return (totalTime + idleTime - 1) / processors + 1;
	}
}
