package se306group8.scheduleoptimizer.algorithm.greedy;

/**
 * Internal representation of a task allocation onto a processor used by greedy algorithm
 */
class ProcessAllocation {
	final int startTime;
	final int processor;
	final int endTime;
	
	public ProcessAllocation(int startTime, int endTime, int processor) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.processor = processor;
	}
}