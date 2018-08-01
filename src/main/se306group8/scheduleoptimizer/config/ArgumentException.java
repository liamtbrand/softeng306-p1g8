package se306group8.scheduleoptimizer.config;

/**
 * ArgumentException is used when there exists an issue
 * with the formatting or parsing of arguments supplied to ArgsParser,
 * as well as ConfigBuilder.
 *
 */
@SuppressWarnings("serial")
public class ArgumentException extends Exception {

	private String message;
	
	public ArgumentException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
}
