package se306group8.scheduleoptimizer.config;

/**
 * The interface for the Config objects.
 * This interface provides access to a configuration object representing the arguments parsed by ArgsParser.
 *
 */
public interface Config {

	/**
	 * INPUT File.
	 */
	public String inputFile();
	
	/**
	 * Number of processors to schedule on.
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
