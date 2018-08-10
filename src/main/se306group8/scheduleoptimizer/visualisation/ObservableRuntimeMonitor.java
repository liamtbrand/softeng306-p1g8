package se306group8.scheduleoptimizer.visualisation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class ObservableRuntimeMonitor implements RuntimeMonitor, Observable {
	
	private Schedule schedule;
	
	private boolean started;
	
	private final List<InvalidationListener> listeners;
	
	private final ReentrantReadWriteLock lock;
	
	public ObservableRuntimeMonitor() {
		lock = new ReentrantReadWriteLock();
		started = false;
		listeners = new ArrayList<>();
	}

	@Override
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		
	}

	@Override
	public void start() {
		Platform.runLater(() -> {
			try {
				lock.writeLock().lock();
				started = true;
				for(InvalidationListener listener : listeners) {
					listener.invalidated(this);
				}
			} finally {
				lock.writeLock().unlock();
			}
		});
	}

	@Override
	public void finish(Schedule solution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logMessage(String message) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean hasStarted() {
		try {
			lock.readLock().lock();
			return started;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void addListener(InvalidationListener listener) {
		Platform.runLater(() -> {
			try {
				lock.writeLock().lock();
				listeners.add(listener);
			} finally {
				lock.writeLock().unlock();
			}
		});
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		Platform.runLater(() -> {
			try {
				lock.writeLock().lock();
				listeners.remove(listener);
			} finally {
				lock.writeLock().unlock();
			}
		});
	}

}
