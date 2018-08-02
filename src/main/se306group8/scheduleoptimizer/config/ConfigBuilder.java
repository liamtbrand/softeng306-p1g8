package se306group8.scheduleoptimizer.config;

/**
 * A builder that is used to build a Config object.
 * ConfigBuilder will perform check on the configuration being applied.
 * If there are issues, the set methods will throw ArgumentException.
 *
 */
public class ConfigBuilder {
	
	private DefaultConfig config;
	
	private boolean setOutputFile;

	public ConfigBuilder() {
		reset();
	}
	
	public void reset() {
		config = new DefaultConfig();
		setOutputFile = false;
	}
	
	public ConfigBuilder setInputFile(String inputFile) throws ArgumentException {
		
		// TODO : Validate inputFile is a path.
		
		config.inputFile = inputFile;
		if(!inputFile.substring(inputFile.length()-4, inputFile.length()).equals(".dot")) {
			throw new ArgumentException("Input file must be a .dot file.");
		}
		if(setOutputFile == false) {
			config.outputFile = inputFile.substring(0, inputFile.length()-4)+"-output.dot";
		}
		return this;
	}
	
	public ConfigBuilder setP(int P) throws ArgumentException {
		if(P <= 0) {
			throw new ArgumentException("P must be a +ve integer value.");
		}
		config.P = P;
		return this;
	}
	
	public ConfigBuilder setN(int N) throws ArgumentException {
		if(N <= 0) {
			throw new ArgumentException("N must be a +ve integer value.");
		}
		config.N = N;
		return this;
	}
	
	public ConfigBuilder setVisualize(boolean visualize) {
		config.visualize = visualize;
		return this;
	}
	
	public ConfigBuilder setOutputFile(String outputFile) {
		
		// TODO : Validate outputFile is a path.
		
		config.outputFile = outputFile;
		setOutputFile = true;
		return this;
	}
	
	public Config build() {
		Config conf = config;
		reset();
		return conf;
	}
	
}
