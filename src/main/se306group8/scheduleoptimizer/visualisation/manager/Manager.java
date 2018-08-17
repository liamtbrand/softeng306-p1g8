package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.TimerTask;

import javafx.application.Platform;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public abstract class Manager extends TimerTask {
	public Manager() {
		FXApplication.getMonitor().addListener((l) -> updateHook((ObservableRuntimeMonitor) l));
	}
	
	protected abstract void updateHook(ObservableRuntimeMonitor monitor);

	@Override
	public final void run() {
		Platform.runLater(() -> {
			updateHook(FXApplication.getMonitor());
		});
	}
}
