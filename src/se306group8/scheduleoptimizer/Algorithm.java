package se306group8.scheduleoptimizer;

public interface Algorithm {

	public Schedule produceCompleteSchedule( TaskGraph graph, int numberOfProcessors );
	
	public void setMonitor( RuntimeMonitor monitor );
	
}
