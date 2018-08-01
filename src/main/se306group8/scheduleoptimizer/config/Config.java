package se306group8.scheduleoptimizer.config;

public interface Config {

	/**
	 * INPUT File.
	 */
	public String inputFile();
	
	/**
	 * Number of processors.
	 */
	public int P();
	
	/**
	 * Number of cores for parallel execution.
	 */
	public int N();
	
	/**
	 * Enable visualization.
	 */
	public boolean visualize();
	
	/**
	 * Output File.
	 */
	public String outputFile();
	
}
