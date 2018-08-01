package se306group8.scheduleoptimizer.config;

public class DefaultConfig implements Config {
	
	protected String inputFile = "INPUT.dot";			// INPUT file.
	protected int P = 1;								// Number of processors.
	protected int N = 1;								// Number of cores for parallel execution.
	protected boolean visualize = false;				// Enable visualization.
	protected String outputFile = "INPUT-output.dot";	// Output file.
	
	protected DefaultConfig() {
		
	}
	
	@Override
	public String inputFile() {
		return inputFile;
	}
	
	@Override
	public int P() {
		return P;
	}
	
	@Override
	public int N() {
		return N;
	}
	
	@Override
	public boolean visualize() {
		return visualize;
	}
	
	@Override
	public String outputFile() {
		return outputFile;
	}

}
