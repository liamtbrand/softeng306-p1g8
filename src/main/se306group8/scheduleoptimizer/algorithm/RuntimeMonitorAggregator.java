package se306group8.scheduleoptimizer.algorithm;

import java.util.Arrays;
import java.util.List;

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
	public void setSchedulesExplored(int number) {
		runtimeMonitors.forEach(m -> m.setSchedulesExplored(number));
	}

	@Override
	public void setSchedulesInArray(int number) {
		runtimeMonitors.forEach(m -> m.setSchedulesInArray(number));
	}

	@Override
	public void setScheduleInArrayStorageSize(int bytes) {
		runtimeMonitors.forEach(m -> m.setScheduleInArrayStorageSize(bytes));
	}

	@Override
	public void setSchedulesInQueue(int number) {
		runtimeMonitors.forEach(m -> m.setSchedulesInQueue(number));
	}

	@Override
	public void setScheduleInQueueStorageSize(int bytes) {
		runtimeMonitors.forEach(m -> m.setScheduleInQueueStorageSize(bytes));
	}

	@Override
	public void setSchedulesOnDisk(int number) {
		runtimeMonitors.forEach(m -> m.setSchedulesOnDisk(number));
	}

	@Override
	public void setScheduleOnDiskStorageSize(int bytes) {
		runtimeMonitors.forEach(m -> m.setScheduleOnDiskStorageSize(bytes));
	}
	
	@Override
	public void setNumberOfProcessors(int processors) {
		runtimeMonitors.forEach(m -> m.setNumberOfProcessors(processors));
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
	public void setAlgorithmName(String name) {
		runtimeMonitors.forEach(m -> m.setAlgorithmName(name));
	}

	@Override
	public void setParallelized(int cores) {
		runtimeMonitors.forEach(m -> m.setParallelized(cores));
	}

}
