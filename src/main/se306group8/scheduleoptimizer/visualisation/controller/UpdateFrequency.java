package se306group8.scheduleoptimizer.visualisation.controller;

public enum UpdateFrequency {
	FAST(30), MEDIUM(250), SLOW(1000);
	
	public final int period;
	
	private UpdateFrequency(int period) {
		this.period = period;
	}
}