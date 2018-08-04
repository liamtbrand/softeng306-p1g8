package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.algorithm.greedy.GreedySchedulingAlgorithm;

public class AlgorithmFactory {
	private final int processors;

	public AlgorithmFactory(int processors) {
		this.processors = processors;
	}
	
	public Algorithm getAlgorithm() {
		return new GreedySchedulingAlgorithm();
	}
}
