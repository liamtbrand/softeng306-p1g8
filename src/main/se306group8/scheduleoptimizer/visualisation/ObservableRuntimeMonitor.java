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

	@Override
	public void setSchedulesInArray(int number) {
		schedulesInArray = number;
	}

	@Override
	public void setScheduleInArrayStorageSize(int bytes) {
		scheduleInArrayStorageSize = bytes;
	}

	@Override
	public void setSchedulesInQueue(int number) {
		schedulesInQueue = number;
	}

	@Override
	public void setScheduleInQueueStorageSize(int bytes) {
		scheduleInQueueStorageSize = bytes;
	}

	@Override
	public void setSchedulesOnDisk(int number) {
		schedulesOnDisk = number;
	}

	@Override
	public void setScheduleOnDiskStorageSize(int bytes) {
		scheduleOnDiskStorageSize = bytes;
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
