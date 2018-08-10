package se306group8.scheduleoptimizer.visualisation;

import java.util.Arrays;
import java.util.List;

import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class RuntimeMonitorAggregator implements RuntimeMonitor {
	
	private final List<RuntimeMonitor> runtimeMonitors;
	
	public RuntimeMonitorAggregator(RuntimeMonitor... runtimeMonitor) {
		runtimeMonitors = Arrays.asList(runtimeMonitor);
	}

	@Override
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		runtimeMonitors.forEach(m -> m.updateBestSchedule(optimalSchedule));
	}

	@Override
	public void start() {
		runtimeMonitors.forEach(m -> m.start());
	}

	@Override
	public void finish(Schedule solution) {
		runtimeMonitors.forEach(m -> m.finish(solution));
	}

	@Override
	public void logMessage(String message) {
		runtimeMonitors.forEach(m -> m.logMessage(message));
	}

	@Override
	public void setSolutionsExplored(int number) {
		runtimeMonitors.forEach(m -> m.setSolutionsExplored(number));
	}
}
