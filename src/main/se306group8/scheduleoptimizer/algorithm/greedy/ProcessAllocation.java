package se306group8.scheduleoptimizer.algorithm.greedy;

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