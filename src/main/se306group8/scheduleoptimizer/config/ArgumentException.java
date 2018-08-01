package se306group8.scheduleoptimizer.config;

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
