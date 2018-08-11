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
		invalidateListeners();
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
