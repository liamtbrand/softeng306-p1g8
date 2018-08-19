package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class StubRuntimeMonitor implements RuntimeMonitor {

	@Override
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		// Do Nothing
	}

	@Override
	public void start(String name, String graphName, int numberOfProcessors, int coresToUseForExecution) {
		// Do Nothing
	}

	@Override
	public void finish(Schedule solution) {
		// Do Nothing
	}

	@Override
	public void logMessage(String message) {
		// Do Nothing
	}

	@Override
	public void setSchedulesExplored(long number) {
		// Do Nothing
	}

	@Override
	public void setSchedulesInArray(long number) {
		// Do Nothing
	}

	@Override
	public void setScheduleInArrayStorageSize(int bytes) {
		// Do Nothing
	}

	@Override
	public void setSchedulesInQueue(long number) {
		// Do Nothing
	}

	@Override
	public void setScheduleInQueueStorageSize(int bytes) {
		// Do Nothing
	}

	@Override
	public void setSchedulesOnDisk(long number) {
		// Do Nothing
	}

	@Override
	public void setScheduleOnDiskStorageSize(int bytes) {
		// Do Nothing
	}

}
