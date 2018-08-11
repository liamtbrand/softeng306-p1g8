package se306group8.scheduleoptimizer.visualisation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class ObservableRuntimeMonitor implements RuntimeMonitor, Observable {

	private final ReentrantReadWriteLock lock;

	private volatile boolean started;
	private volatile boolean finished;
	private volatile TreeSchedule bestSchedule;
	private volatile Queue<String> messages;
	
	private final List<InvalidationListener> listeners;
	
	public ObservableRuntimeMonitor() {

		lock = new ReentrantReadWriteLock();

		started = false;
		finished = false;
		bestSchedule = null;
		messages = new LinkedList<>();

		listeners = new ArrayList<>();
	}

	private void invalidateListeners() {
		Platform.runLater(() -> {
			try {
				lock.writeLock().lock();
				for (InvalidationListener listener : listeners) {
					listener.invalidated(this);
				}
			} finally {
				lock.writeLock().unlock();
			}
		});
	}

	@Override
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		try {
			lock.writeLock().lock();
			bestSchedule = optimalSchedule;
			invalidateListeners();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void start() {
		try {
			lock.writeLock().lock();
			started = true;
			invalidateListeners();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void finish(Schedule solution) {
		try {
			lock.writeLock().lock();
			finished = true;
			invalidateListeners();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void logMessage(String message) {
		try {
			lock.writeLock().lock();
			messages.add(message);
			invalidateListeners();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public boolean hasStarted() {
		try {
			lock.readLock().lock();
			return started;
		} finally {
			lock.readLock().unlock();
		}
	}

	public boolean hasFinished() {
		try {
			lock.readLock().lock();
			return finished;
		} finally {
			lock.readLock().unlock();
		}
	}

	public boolean hasMessages() {
		try {
			lock.readLock().lock();
			return messages.size() > 0;
		} finally {
			lock.readLock().unlock();
		}
	}

	public String nextMessage() {
		try {
			lock.writeLock().lock();
			return messages.poll();
		} finally {
			lock.writeLock().unlock();
		}
	}

	public TreeSchedule getBestSchedule() {
		try {
			lock.readLock().lock();
			return bestSchedule;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void addListener(InvalidationListener listener) {
		try {
			lock.writeLock().lock();
			listeners.add(listener);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		try {
			lock.writeLock().lock();
			listeners.remove(listener);
		} finally {
			lock.writeLock().unlock();
		}
	}

}
