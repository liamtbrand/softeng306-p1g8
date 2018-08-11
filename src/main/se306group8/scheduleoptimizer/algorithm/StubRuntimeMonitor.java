package se306group8.scheduleoptimizer.algorithm;

import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class StubRuntimeMonitor implements RuntimeMonitor {

	@Override
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		// Do Nothing
	}

	@Override
	public void start() {
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
	public void setSolutionsExplored(int number) {
		// Do Nothing
	}

	@Override
	public void setSchedulesInArray(int number) {
		// Do Nothing
	}

	@Override
	public void setScheduleInArrayStorageSize(int bytes) {
		// Do Nothing
	}

	@Override
	public void setSchedulesInQueue(int number) {
		// Do Nothing
	}

	@Override
	public void setScheduleInQueueStorageSize(int bytes) {
		// Do Nothing
	}

	@Override
	public void setSchedulesOnDisk(int number) {
		// Do Nothing
	}

	@Override
	public void setScheduleOnDiskStorageSize(int bytes) {
		// Do Nothing
	}

}
