package se306group8.scheduleoptimizer.algorithm;

import java.util.Arrays;
import java.util.List;

import se306group8.scheduleoptimizer.taskgraph.Schedule;

/** This is a runtime monitor that passes calls to an array of children monitors */
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
	public void start(String name, String graphName, int numberOfProcessors, int coresToUseForExecution) {
		runtimeMonitors.forEach(m -> m.start(name, graphName, numberOfProcessors, coresToUseForExecution));
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
	public void setSchedulesExplored(long number) {
		runtimeMonitors.forEach(m -> m.setSchedulesExplored(number));
	}

	@Override
	public void setSchedulesInArray(long number) {
		runtimeMonitors.forEach(m -> m.setSchedulesInArray(number));
	}

	@Override
	public void setScheduleInArrayStorageSize(int bytes) {
		runtimeMonitors.forEach(m -> m.setScheduleInArrayStorageSize(bytes));
	}

	@Override
	public void setSchedulesInQueue(long number) {
		runtimeMonitors.forEach(m -> m.setSchedulesInQueue(number));
	}

	@Override
	public void setScheduleInQueueStorageSize(int bytes) {
		runtimeMonitors.forEach(m -> m.setScheduleInQueueStorageSize(bytes));
	}

	@Override
	public void setSchedulesOnDisk(long number) {
		runtimeMonitors.forEach(m -> m.setSchedulesOnDisk(number));
	}

	@Override
	public void setScheduleOnDiskStorageSize(int bytes) {
		runtimeMonitors.forEach(m -> m.setScheduleOnDiskStorageSize(bytes));
	}
	
	@Override
	public boolean isInterupted() {
		boolean interupted = false;
		for (RuntimeMonitor m:runtimeMonitors) {
			interupted = interupted || m.isInterupted();
		}
		return interupted;
	}

	@Override
	public void setBucketSize(int size) {
		runtimeMonitors.forEach(m -> m.setBucketSize(size));
	}
	
	@Override
	public void setScheduleDistribution(int[] distribution, int limit) {
		runtimeMonitors.forEach(m -> m.setScheduleDistribution(distribution, limit));
	}

	@Override
	public void setUpperBound(int bound) {
		runtimeMonitors.forEach(m -> m.setUpperBound(bound));
	}
	
	@Override
	public void setLowerBound(int lowerBound) {
		runtimeMonitors.forEach(m -> m.setLowerBound(lowerBound));
	}
}
