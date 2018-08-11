package se306group8.scheduleoptimizer.visualisation;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class ObservableRuntimeMonitor implements RuntimeMonitor, Observable {

	private volatile boolean started;
	private volatile boolean finished;
	private volatile TreeSchedule bestSchedule;
	private volatile Queue<String> messages;

	private volatile int solutionsExplored;
	private volatile int schedulesInArray;
	private volatile int scheduleInArrayStorageSize;
	private volatile int schedulesInQueue;
	private volatile int scheduleInQueueStorageSize;
	private volatile int schedulesOnDisk;
	private volatile int scheduleOnDiskStorageSize;
	
	private final List<InvalidationListener> listeners;
	
	public ObservableRuntimeMonitor() {

		started = false;
		finished = false;
		bestSchedule = null;
		messages = new LinkedBlockingQueue<>();

		solutionsExplored = 0;
		schedulesInArray = 0;
		scheduleInArrayStorageSize = 0;
		schedulesInQueue = 0;
		scheduleInQueueStorageSize = 0;
		schedulesOnDisk = 0;
		scheduleOnDiskStorageSize = 0;

		listeners = new ArrayList<>();
	}

	private void invalidateListeners() {
		Platform.runLater(() -> {
			for (InvalidationListener listener : listeners) {
				listener.invalidated(this);
			}
		});
	}

	@Override
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		bestSchedule = optimalSchedule;
	}

	@Override
	public void start() {
		started = true;
		invalidateListeners();
	}

	@Override
	public void finish(Schedule solution) {
		finished = true;
		invalidateListeners();
	}

	@Override
	public void logMessage(String message) {
		messages.add(message);
		invalidateListeners();
	}

	@Override
	public void setSolutionsExplored(int number) {
		solutionsExplored = number;
	}

	public int getSolutionsExplored() {
		return solutionsExplored;
	}

	@Override
	public void setSchedulesInArray(int number) {
		schedulesInArray = number;
	}

	public int getSchedulesInArray() {
		return schedulesInArray;
	}

	@Override
	public void setScheduleInArrayStorageSize(int bytes) {
		scheduleInArrayStorageSize = bytes;
	}

	public int getScheduleInArrayStorageSize() {
		return scheduleInArrayStorageSize;
	}

	@Override
	public void setSchedulesInQueue(int number) {
		schedulesInQueue = number;
	}

	public int getSchedulesInQueue() {
		return schedulesInQueue;
	}

	@Override
	public void setScheduleInQueueStorageSize(int bytes) {
		scheduleInQueueStorageSize = bytes;
	}

	public int getScheduleInQueueStorageSize() {
		return scheduleInQueueStorageSize;
	}

	@Override
	public void setSchedulesOnDisk(int number) {
		schedulesOnDisk = number;
	}

	public int getSchedulesOnDisk() {
		return schedulesOnDisk;
	}

	@Override
	public void setScheduleOnDiskStorageSize(int bytes) {
		scheduleOnDiskStorageSize = bytes;
	}

	public int getScheduleOnDiskStorageSize() {
		return scheduleOnDiskStorageSize;
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean hasFinished() {
		return finished;
	}

	public String nextMessage() {
		return messages.poll();
	}

	public TreeSchedule getBestSchedule() {
		return bestSchedule;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		listeners.remove(listener);
	}

}
