package se306group8.scheduleoptimizer.config;

public class ConfigBuilder {
	
	private DefaultConfig config;
	
	private boolean setOutputFile;

	public ConfigBuilder() {
		reset();
	}
	
	private void reset() {
		config = new DefaultConfig();
		setOutputFile = false;
	}
	
	public ConfigBuilder setInputFile(String inputFile) {
		config.inputFile = inputFile;
		if(setOutputFile == false) {
			config.outputFile = inputFile.substring(0, inputFile.length()-4)+"-output.dot";
		}
		return this;
	}
	
	public ConfigBuilder setP(int P) {
		config.P = P;
		return this;
	}
	
	public ConfigBuilder setN(int N) {
		config.N = N;
		return this;
	}
	
	public ConfigBuilder setVisualize(boolean visualize) {
		config.visualize = visualize;
		return this;
	}
	
	public ConfigBuilder setOutputFile(String outputFile) {
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
